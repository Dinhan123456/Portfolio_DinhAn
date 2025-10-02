package com.canhan.portfolio.controller;

import com.canhan.portfolio.dto.ChatRequest;
import com.canhan.portfolio.dto.ChatResponse;
import com.canhan.portfolio.service.interfaces.TarotAIService;
import jakarta.servlet.http.HttpSession; // Thêm import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TarotAIController {

    private final TarotAIService tarotAIService;

    @Autowired
    public TarotAIController(TarotAIService tarotAIService) {
        this.tarotAIService = tarotAIService;
    }

    @PostMapping("/tarot-chat")
    // Sửa lại phương thức để nhận HttpSession
    public ChatResponse handleChat(@RequestBody ChatRequest chatRequest, HttpSession session) {
        // Truyền cả câu hỏi và session xuống service
        String aiResponse = tarotAIService.getTarotReading(chatRequest.getMessage(), session);
        return new ChatResponse(aiResponse);
    }

    @PostMapping("/tarot-chat/reset")
    public ChatResponse resetChat(HttpSession session) {
        // Xóa toàn bộ bối cảnh của cuộc trò chuyện cũ
        session.removeAttribute("tarotContext");
        // Trả về câu chào mừng đầu tiên để bắt đầu cuộc trò chuyện mới
        String welcomeMessage = "Chào bạn, để bắt đầu trải bài Tarot, bạn vui lòng cho tôi biết tên của bạn là gì?";
        return new ChatResponse(welcomeMessage);
    }
}