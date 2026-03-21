package com.example.weconnect.data;

import com.example.weconnect.R;
import com.example.weconnect.models.ChatMessage;
import com.example.weconnect.models.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class FakeChatRepository {

    private static FakeChatRepository instance;

    private final List<ChatRoom> chatRooms = new ArrayList<>();

    private FakeChatRepository() {
        seedRooms();
    }

    public static synchronized FakeChatRepository getInstance() {
        if (instance == null) {
            instance = new FakeChatRepository();
        }
        return instance;
    }

    public List<ChatRoom> getChatRooms() {
        return new ArrayList<>(chatRooms);
    }

    public List<ChatRoom> searchChatRooms(String query) {
        return searchChatRoomsByType(null, query);
    }

    public List<ChatRoom> getGroupChatRooms() {
        return searchChatRoomsByType(ChatRoom.TYPE_GROUP, "");
    }

    public List<ChatRoom> getDirectChatRooms() {
        return searchChatRoomsByType(ChatRoom.TYPE_DIRECT, "");
    }

    public List<ChatRoom> searchChatRoomsByType(String type, String query) {
        List<ChatRoom> results = new ArrayList<>();
        String normalized = query == null ? "" : query.trim().toLowerCase();

        for (ChatRoom room : chatRooms) {
            boolean matchesType = type == null || type.equals(room.getType());
            if (!matchesType) {
                continue;
            }

            if (normalized.isEmpty()) {
                results.add(room);
                continue;
            }

            String title = room.getTitle() == null ? "" : room.getTitle().toLowerCase();
            String preview = room.getLastMessagePreview() == null ? "" : room.getLastMessagePreview().toLowerCase();
            if (title.contains(normalized) || preview.contains(normalized)) {
                results.add(room);
            }
        }
        return results;
    }

    public ChatRoom getRoomById(String roomId) {
        for (ChatRoom room : chatRooms) {
            if (room.getId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    public void sendMessage(String roomId, String senderName, String content) {
        ChatRoom room = getRoomById(roomId);
        if (room == null) {
            return;
        }
        room.addMessage(new ChatMessage(
                String.valueOf(System.currentTimeMillis()),
                senderName,
                content,
                "Now",
                true
        ));
    }

    private void seedRooms() {
        List<ChatMessage> coffeeMessages = new ArrayList<>();
        coffeeMessages.add(new ChatMessage("m1", "Minh Hoang", "I can join around 7pm.", "09:20", false));
        coffeeMessages.add(new ChatMessage("m2", "Quynh Nguyen", "Great, I booked a table for 4.", "09:23", true));

        List<ChatMessage> codeMessages = new ArrayList<>();
        codeMessages.add(new ChatMessage("m3", "Lan Anh", "Let's split the UI tasks first.", "Yesterday", false));
        codeMessages.add(new ChatMessage("m4", "Quynh Nguyen", "I will handle the feed and profile flow.", "Yesterday", true));

        List<ChatMessage> directMessages = new ArrayList<>();
        directMessages.add(new ChatMessage("m5", "Minh Hoang", "Do you want to review the mockup tonight?", "10:12", false));
        directMessages.add(new ChatMessage("m6", "Quynh Nguyen", "Yes, send it over and I will check it.", "10:15", true));

        chatRooms.add(new ChatRoom(
                "room_group_coffee",
                "Coffee Meetup",
                ChatRoom.TYPE_GROUP,
                R.drawable.ic_user_placeholder,
                true,
                "",
                coffeeMessages
        ));

        chatRooms.add(new ChatRoom(
                "room_group_code",
                "Design and Code Crew",
                ChatRoom.TYPE_GROUP,
                R.drawable.ic_user_placeholder,
                false,
                "Hoat dong 20 phut truoc",
                codeMessages
        ));

        chatRooms.add(new ChatRoom(
                "room_direct_minh",
                "Minh Hoang",
                ChatRoom.TYPE_DIRECT,
                R.drawable.ic_user_placeholder,
                true,
                "",
                directMessages
        ));
    }
}
