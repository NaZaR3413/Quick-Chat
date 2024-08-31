package com.example.quickchat.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import com.example.quickchat.springboot.controller.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer{
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // creating prefixes 
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/chatroom", "/user");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // path for websocket connections
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
            .setInterceptors(new HttpSessionHandshakeInterceptor() {
                        @Override
                        public void afterHandshake(
                                ServerHttpRequest request,
                                ServerHttpResponse response,
                                WebSocketHandler wsHandler,
                                Exception ex) {
                        }
                    });
    }

    // user connections handler 
    public class CustomWebSocketHandler extends TextWebSocketHandler 
    {
        private ChatController chatController;

        public CustomWebSocketHandler(ChatController _chatController)
        {
            chatController = _chatController;
        }

        public void afterConnectionEstablished(WebSocketSession session) throws Exception
        {
            String username = getUsernameFromSession(session); // TO-DO - finish implementation
            chatController.registerUser(username, session);
        }

        public void afterConnectionClosed(WebSocketSession session) throws Exception
        {
            String username = getUsernameFromSession(session); // TO-DO - finish implementation
            chatController.unregisterUser(username);
        }

        // TO-DO implement a way to get a username from the current session 
        public String getUsernameFromSession(WebSocketSession session)
        {
            return "username";
        }
    }
}
