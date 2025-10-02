package com.canhan.portfolio.service.interfaces;

import com.canhan.portfolio.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages();

    Message findByEmail(String theEmail);

    Message save(Message message);

    void deleteById(Integer id);
}