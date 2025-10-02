package com.canhan.portfolio.dto;

public class ChatResponse {
    private String response;

    public ChatResponse(String response) {
        this.response = response;
    }

    // Getters and Setters
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}