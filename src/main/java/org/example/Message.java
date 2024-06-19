package org.example;

public class Message {
    public MessageType type;
    public String content;
    public String recipient;

public Message(MessageType type, String content) {
    this.type = type;
    this.content = content;
}

public Message(MessageType type, String content, String recipient) {
    this.type = type;
    this.content = content;
    this.recipient = recipient;
}

}