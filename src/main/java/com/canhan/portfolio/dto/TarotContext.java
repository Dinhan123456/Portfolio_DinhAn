package com.canhan.portfolio.dto;

import java.io.Serializable;
import java.util.List;

// Implement Serializable để đảm bảo đối tượng có thể lưu vào session một cách an toàn
public class TarotContext implements Serializable {

    // Trạng thái của cuộc trò chuyện để AI biết cần hỏi gì tiếp theo
    public enum ConversationState {
        AWAITING_NAME,
        AWAITING_BIRTHDATE,
        AWAITING_TOPIC,
        READY_TO_READ,
        IN_CONVERSATION // Trạng thái sau khi đã giải bài lần đầu
    }

    private List<String> drawnCards;
    private String name;
    private String birthDate;
    private String topic;
    private ConversationState state = ConversationState.AWAITING_NAME; // Bắt đầu bằng việc chờ tên

    // Kiểm tra xem đã có đủ thông tin để giải bài chưa
    public boolean hasAllInfo() {
        return name != null && !name.isEmpty() &&
                birthDate != null && !birthDate.isEmpty() &&
                topic != null && !topic.isEmpty();
    }

    // Getters and Setters
    public List<String> getDrawnCards() { return drawnCards; }
    public void setDrawnCards(List<String> drawnCards) { this.drawnCards = drawnCards; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public ConversationState getState() { return state; }
    public void setState(ConversationState state) { this.state = state; }
}