package com.example.weconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements Serializable {

    public static final String TYPE_GROUP = "group";
    public static final String TYPE_DIRECT = "direct";

    private final String id;
    private final String title;
    private final String type;
    private final int avatarResId;
    private final boolean active;
    private final String inactiveStatusLabel;
    private final List<ChatMessage> messages;

    public ChatRoom(String id, String title, String type, int avatarResId, List<ChatMessage> messages) {
        this(id, title, type, avatarResId, true, "", messages);
    }

    public ChatRoom(String id, String title, String type, int avatarResId, boolean active,
                    String inactiveStatusLabel, List<ChatMessage> messages) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.avatarResId = avatarResId;
        this.active = active;
        this.inactiveStatusLabel = inactiveStatusLabel == null ? "" : inactiveStatusLabel;
        this.messages = new ArrayList<>(messages);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public boolean isActive() {
        return active;
    }

    public String getInactiveStatusLabel() {
        return inactiveStatusLabel;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getLastMessagePreview() {
        if (messages.isEmpty()) {
            return "No messages yet";
        }
        ChatMessage lastMessage = messages.get(messages.size() - 1);
        return lastMessage.getContent();
    }

    public String getLastMessageTime() {
        if (messages.isEmpty()) {
            return "";
        }
        return messages.get(messages.size() - 1).getTimeLabel();
    }

    public String getTypeLabel() {
        if (TYPE_GROUP.equals(type)) {
            return "Hoat dong";
        }
        return "Lien he";
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }
}
