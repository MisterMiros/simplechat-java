package com.example.simplechat.client.services.chat;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatService {

    private SessionHandler sessionHandler;
    private StompSession   stompSession;

    public ChatService(String url) {
        WebSocketClient client = new StandardWebSocketClient();

        sessionHandler = new SessionHandler();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            stompSession = stompClient.connect(url, sessionHandler).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addHandler(MessageEventHandler handler) {
        sessionHandler.addHandler(handler);
    }

    public void removeHandler(MessageEventHandler handler) {
        sessionHandler.removeHandler(handler);
    }

    public void sendMessage(String sender, String message) {
        stompSession.send("/chat/send", new Message(sender, message));
    }

    private class SessionHandler implements StompSessionHandler {

        private List<MessageEventHandler> eventHandlers = new ArrayList<>();

        public void addHandler(MessageEventHandler handler) {
            eventHandlers.add(handler);
        }

        public void removeHandler(MessageEventHandler handler) {
            eventHandlers.remove(handler);
        }

        @Override
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            stompSession.subscribe("/chat/messages", this);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            throw new IllegalStateException(throwable);
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            throw new IllegalStateException(throwable);
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            Message message = (Message) o;
            for (MessageEventHandler handler : eventHandlers) {
                handler.handle(message);
            }
        }
    }

}
