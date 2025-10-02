package com.canhan.portfolio.service;

import com.canhan.portfolio.dto.TarotContext;
import com.canhan.portfolio.service.interfaces.TarotAIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TarotAIServiceImpl implements TarotAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final List<String> VALID_TOPICS = Arrays.asList(
            "tình yêu", "sự nghiệp", "tài chính", "gia đình", "sức khỏe", "phát triển bản thân"
    );

    // ... (Danh sách 78 lá bài Tarot giữ nguyên như cũ)
    private static final List<String> TAROT_DECK = Arrays.asList(
            "The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor",
            "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit",
            "Wheel of Fortune", "Justice", "The Hanged Man", "Death", "Temperance",
            "The Devil", "The Tower", "The Star", "The Moon", "The Sun", "Judgement", "The World",
            "Ace of Wands", "Two of Wands", "Three of Wands", "Four of Wands", "Five of Wands",
            "Six of Wands", "Seven of Wands", "Eight of Wands", "Nine of Wands", "Ten of Wands",
            "Page of Wands", "Knight of Wands", "Queen of Wands", "King of Wands",
            "Ace of Cups", "Two of Cups", "Three of Cups", "Four of Cups", "Five of Cups",
            "Six of Cups", "Seven of Cups", "Eight of Cups", "Nine of Cups", "Ten of Cups",
            "Page of Cups", "Knight of Cups", "Queen of Cups", "King of Cups",
            "Ace of Swords", "Two of Swords", "Three of Swords", "Four of Swords", "Five of Swords",
            "Six of Swords", "Seven of Swords", "Eight of Swords", "Nine of Swords", "Ten of Swords",
            "Page of Swords", "Knight of Swords", "Queen of Swords", "King of Swords",
            "Ace of Pentacles", "Two of Pentacles", "Three of Pentacles", "Four of Pentacles",
            "Five of Pentacles", "Six of Pentacles", "Seven of Pentacles", "Eight of Pentacles",
            "Nine of Pentacles", "Ten of Pentacles", "Page of Pentacles", "Knight of Pentacles",
            "Queen of Pentacles", "King of Pentacles"
    );

    @Override
    public String getTarotReading(String userQuestion, HttpSession session) {
        TarotContext context = (TarotContext) session.getAttribute("tarotContext");

        // Bắt đầu một phiên mới nếu người dùng yêu cầu hoặc chưa có bối cảnh
        boolean isNewSessionRequest = userQuestion.toLowerCase().contains("xem bài") || userQuestion.toLowerCase().contains("bắt đầu lại");
        if (isNewSessionRequest || context == null) {
            context = new TarotContext();
            session.setAttribute("tarotContext", context);
            return "Chào bạn, để bắt đầu trải bài Tarot, bạn vui lòng cho tôi biết tên của bạn là gì?";
        }

        // Luồng hội thoại dựa trên trạng thái
        switch (context.getState()) {
            case AWAITING_NAME:
                context.setName(userQuestion);
                context.setState(TarotContext.ConversationState.AWAITING_BIRTHDATE);
                session.setAttribute("tarotContext", context);
                return "Cảm ơn " + context.getName() + ". Bạn vui lòng cho tôi biết ngày tháng năm sinh của bạn nhé.";

            case AWAITING_BIRTHDATE:
                Pattern datePattern = Pattern.compile("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4})");
                Matcher dateMatcher = datePattern.matcher(userQuestion);
                if (dateMatcher.find()) {
                    context.setBirthDate(dateMatcher.group(1));
                    context.setState(TarotContext.ConversationState.AWAITING_TOPIC);
                    session.setAttribute("tarotContext", context);
                    return "Tuyệt vời. Bây giờ, bạn muốn xem về chủ đề nào? Tôi có thể xem rõ nhất về các lĩnh vực sau: " +
                            "**Tình yêu, Sự nghiệp, Tài chính, Gia đình, Sức khỏe, Phát triển bản thân.**";
                } else {
                    return "Ngày sinh có vẻ chưa đúng định dạng. Bạn vui lòng nhập lại theo dạng DD/MM/YYYY nhé.";
                }

            case AWAITING_TOPIC:
                return handleTopicSelection(userQuestion, context, session);

            case READY_TO_READ:
            case IN_CONVERSATION:
                // Logic để giải bài hoặc trả lời câu hỏi nối tiếp
                String prompt = buildReadingPrompt(userQuestion, context, context.getState() == TarotContext.ConversationState.IN_CONVERSATION);
                context.setState(TarotContext.ConversationState.IN_CONVERSATION);
                session.setAttribute("tarotContext", context);
                return callGeminiApi(prompt);
        }

        return "Có chút nhầm lẫn, chúng ta hãy bắt đầu lại nhé. Bạn muốn xem bài không?";
    }

    private String handleTopicSelection(String userInput, TarotContext context, HttpSession session) {
        List<String> matchedTopics = new ArrayList<>();
        String bestSuggestion = null;

        for (String validTopic : VALID_TOPICS) {
            if (userInput.toLowerCase().contains(validTopic)) {
                matchedTopics.add(validTopic);
            }
        }

        // Xử lý sai chính tả
        if (matchedTopics.isEmpty()) {
            bestSuggestion = findBestTopicMatch(userInput.toLowerCase());
            if (bestSuggestion != null) {
                // Lưu lại gợi ý để chờ xác nhận từ người dùng
                session.setAttribute("topicSuggestion", bestSuggestion);
                return "Có phải bạn muốn hỏi về chủ đề **" + bestSuggestion + "** không? (Vui lòng trả lời 'đúng' hoặc chọn lại chủ đề khác)";
            }
        }

        // Xử lý xác nhận sai chính tả
        String suggestion = (String) session.getAttribute("topicSuggestion");
        if (suggestion != null && (userInput.equalsIgnoreCase("đúng") || userInput.equalsIgnoreCase("yes"))) {
            matchedTopics.add(suggestion);
            session.removeAttribute("topicSuggestion");
        }

        if (matchedTopics.size() == 1) {
            context.setTopic(matchedTopics.get(0));
            context.setState(TarotContext.ConversationState.READY_TO_READ);
            session.setAttribute("tarotContext", context);
            return "Đã rõ. Tôi sẽ rút bài và giải đáp về chủ đề **" + context.getTopic() + "** cho bạn. Hãy chờ trong giây lát...";
        } else if (matchedTopics.size() > 1) {
            return "Năng lượng vũ trụ cần sự tập trung. Để có kết quả chính xác nhất, bạn vui lòng chỉ chọn **một chủ đề duy nhất** trong một lần xem nhé.";
        } else {
            return "Chủ đề bạn chọn chưa nằm trong phạm vi xem bài của tôi. Bạn vui lòng chọn một trong các chủ đề sau: **Tình yêu, Sự nghiệp, Tài chính, Gia đình, Sức khỏe, Phát triển bản thân.**";
        }
    }

    private String buildReadingPrompt(String userQuestion, TarotContext context, boolean isFollowUp) {
        if (context.getDrawnCards() == null) {
            Collections.shuffle(TAROT_DECK);
            context.setDrawnCards(TAROT_DECK.subList(0, 3));
        }
        String cardsString = String.join(", ", context.getDrawnCards());

        String prompt;
        if(isFollowUp) {
            prompt = String.format("Bạn đang trong một cuộc trò chuyện Tarot. Bối cảnh ban đầu là người dùng %s (sinh ngày %s) hỏi về chủ đề '%s' và đã rút được các lá bài: %s. Câu hỏi nối tiếp của họ là: \"%s\". Hãy trả lời câu hỏi này dựa trên bối cảnh đã có. Hãy trả lời bằng tiếng Việt.", context.getName(), context.getBirthDate(), context.getTopic(), cardsString, userQuestion);
        } else {
            prompt = String.format("Bạn là một nhà huyền học Tarot chuyên nghiệp. Hãy xem bài cho %s (sinh ngày %s) về chủ đề '%s'. Các lá bài họ rút được là: %s. Dựa vào tất cả thông tin này, hãy đưa ra một lời giải đáp chi tiết và sâu sắc. Hãy trả lời bằng tiếng Việt.", context.getName(), context.getBirthDate(), context.getTopic(), cardsString);
        }
        return prompt;
    }

    // Thuật toán Levenshtein Distance đơn giản để so sánh sự giống nhau giữa 2 chuỗi
    private int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private String findBestTopicMatch(String userInput) {
        String bestMatch = null;
        int minDistance = 2; // Chỉ chấp nhận sai tối đa 2 ký tự

        for (String topic : VALID_TOPICS) {
            int distance = calculateLevenshteinDistance(userInput, topic);
            if (distance <= minDistance) {
                minDistance = distance;
                bestMatch = topic;
            }
        }
        return bestMatch;
    }

    private String callGeminiApi(String prompt) {
        // ... (Giữ nguyên phương thức callGeminiApi của bạn)
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
//            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

            String requestBody = "{\"contents\":[{\"parts\":[{\"text\": \"" + prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}]}";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            GeminiResponse response = restTemplate.postForObject(apiUrl, entity, GeminiResponse.class);

            if (response != null && response.candidates != null && response.candidates.length > 0) {
                return response.candidates[0].content.parts[0].text.trim();
            }
            return "Có vẻ như các vì sao chưa sẵn sàng trả lời. Vui lòng thử lại.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, có một chút nhiễu loạn năng lượng khiến tôi không thể kết nối. Vui lòng thử lại sau.";
        }
    }

    // Các lớp lồng nhau để khớp với cấu trúc JSON của Gemini
    private static class GeminiResponse { public Candidate[] candidates; }
    private static class Candidate { public Content content; }
    private static class Content { public Part[] parts; }
    private static class Part { public String text; }
}