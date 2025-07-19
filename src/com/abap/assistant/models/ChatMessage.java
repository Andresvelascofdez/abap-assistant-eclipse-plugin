package com.abap.assistant.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a chat message in the conversation
 */
public class ChatMessage {
    
    private String id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;
    
    public enum MessageType {
        USER, ASSISTANT, SYSTEM, ERROR, QUICK_ACTION
    }
    
    public ChatMessage(String role, String content) {
        this.id = UUID.randomUUID().toString();
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.type = mapRoleToType(role);
    }
    
    public ChatMessage(String role, String content, MessageType type) {
        this.id = UUID.randomUUID().toString();
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }
    
    private MessageType mapRoleToType(String role) {
        switch (role.toLowerCase()) {
            case "user": return MessageType.USER;
            case "assistant": return MessageType.ASSISTANT;
            case "system": return MessageType.SYSTEM;
            default: return MessageType.USER;
        }
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
        this.type = mapRoleToType(role);
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp.toString(), role, content);
    }
}
