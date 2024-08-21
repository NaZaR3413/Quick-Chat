package com.example.quickchat.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.quickchat.springboot.model.Message;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    // return public messages to each user 
    @MessageMapping("/message") // /app/message
    @SendTo("/chatroom/public") 
    public Message recievePublicMessage(@Payload Message message) 
    {
        return message;
    }

    // return private message to specific user
    @MessageMapping("/private-message")
    public Message recievePrivateMessage(@Payload Message message)
    {
        // /user/<NAME>/private
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/priavate", message);
        return message;
    }
}
