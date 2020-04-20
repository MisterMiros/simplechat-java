module com.example.simplechat {
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    requires spring.core;
    requires spring.websocket;
    requires spring.messaging;
    exports com.example.simplechat.client;
    exports com.example.simplechat.client.services.chat;
}