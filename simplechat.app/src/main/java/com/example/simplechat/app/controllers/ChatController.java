package com.example.simplechat.app.controllers;

import com.example.simplechat.app.objects.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/send")
    @SendTo("/chat/messages")
    public Message send(Message message) {
        return message;
    }

}
