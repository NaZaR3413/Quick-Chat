package com.example.quickchat.springboot.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import com.example.quickchat.springboot.model.Message;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // In memory storage for user management
    private ConcurrentHashMap<String, WebSocketSession> connectedUsers = new ConcurrentHashMap<String, WebSocketSession>();
    
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
        // verify user's existence and send the message
        String receiver = message.getReceiverName();
        if(connectedUsers.containsKey(receiver))
        {
            // /user/<NAME>/private
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/priavate", message);
        }
        else
        {
            System.out.println("User " + receiver + " is not connected");
        }

        return message;
    }

    // register a new user when they connect
    public void registerUser(String username, WebSocketSession session)
    {
        connectedUsers.put(username, session);
        simpMessagingTemplate.convertAndSend("/chatroom/public", username + " has connected");
    }

    // unregister a user when they disconnect 
    public void unregisterUser(String username)
    {
        // verify username's existence and remove it 
        if(connectedUsers.containsKey(username))
        {
            connectedUsers.remove(username);
            simpMessagingTemplate.convertAndSend("/chatroom/public", username + " has disconnected");
        }
        else
        {
            System.out.println(username + " does not exist, removal unsuccessful");
        }
    }

    // brodcast current userlist to all connected clients
    private void brodcastUserList()
    {
        simpMessagingTemplate.convertAndSend("/chatroom/public", connectedUsers.keySet());
    }
}
