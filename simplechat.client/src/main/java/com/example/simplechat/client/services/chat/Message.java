package com.example.simplechat.client.services.chat;

public class Message {

    public String sender;
    public String text;

    public Message() {
    }

    public Message(String sender, String text) {
        this.sender = sender;
        this.text   = text;
    }
}
