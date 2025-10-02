package com.canhan.portfolio.service.interfaces;

import jakarta.servlet.http.HttpSession; // Thêm import này

public interface TarotAIService {
    // Thêm HttpSession vào tham số
    String getTarotReading(String userQuestion, HttpSession session);
}