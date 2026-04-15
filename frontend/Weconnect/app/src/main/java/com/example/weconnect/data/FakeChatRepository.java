package com.example.weconnect.data;

import com.example.weconnect.R;
import com.example.weconnect.models.ChatMessage;
import com.example.weconnect.models.ChatRoom;

import java.util.ArrayList;
import java.util.Arrays;
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
            boolean matchesType;
            if (type == null) {
                matchesType = true;
            } else if (ChatRoom.TYPE_DIRECT.equals(type)) {
                // "Liên hệ" tab shows both direct and friend_group
                matchesType = ChatRoom.TYPE_DIRECT.equals(room.getType())
                        || ChatRoom.TYPE_FRIEND_GROUP.equals(room.getType());
            } else {
                matchesType = type.equals(room.getType());
            }

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

    public ChatRoom findDirectRoom(String participantName) {
        for (ChatRoom room : chatRooms) {
            if (ChatRoom.TYPE_DIRECT.equals(room.getType()) &&
                    room.getTitle() != null &&
                    room.getTitle().equalsIgnoreCase(participantName)) {
                return room;
            }
        }
        return null;
    }

    public ChatRoom getOrCreateDirectRoom(String participantName) {
        ChatRoom existing = findDirectRoom(participantName);
        if (existing != null) return existing;

        String currentUser = FakePostRepository.getInstance().getCurrentUsername();
        ChatRoom newRoom = new ChatRoom(
                "room_direct_" + participantName.toLowerCase().replaceAll("\\s+", "_"),
                participantName,
                ChatRoom.TYPE_DIRECT,
                R.drawable.ic_user_placeholder,
                true,
                "",
                new ArrayList<>(),
                currentUser,
                new ArrayList<>(Arrays.asList(currentUser, participantName)),
                new ArrayList<>()
        );
        chatRooms.add(newRoom);
        return newRoom;
    }

    public ChatRoom createGroupChat(String title, List<String> members) {
        // Check for existing group chat with same title
        ChatRoom existing = findGroupRoomByTitle(title);
        if (existing != null) {
            return existing;
        }

        String currentUser = FakePostRepository.getInstance().getCurrentUsername();
        List<String> allMembers = new ArrayList<>();
        allMembers.add(currentUser);
        for (String member : members) {
            if (!member.equalsIgnoreCase(currentUser)) {
                allMembers.add(member);
            }
        }

        String roomId = "room_friend_group_" + System.currentTimeMillis();
        ChatRoom newRoom = new ChatRoom(
                roomId,
                title,
                ChatRoom.TYPE_FRIEND_GROUP,
                R.drawable.ic_user_placeholder,
                true,
                "",
                new ArrayList<>(),
                currentUser,
                allMembers,
                new ArrayList<>()
        );
        chatRooms.add(0, newRoom);
        return newRoom;
    }

    public ChatRoom findGroupRoomByTitle(String title) {
        for (ChatRoom room : chatRooms) {
            if ((ChatRoom.TYPE_GROUP.equals(room.getType()) || ChatRoom.TYPE_FRIEND_GROUP.equals(room.getType())) &&
                    room.getTitle() != null &&
                    room.getTitle().equalsIgnoreCase(title)) {
                return room;
            }
        }
        return null;
    }

    /**
     * Tạo hoặc lấy nhóm chat hoạt động (TYPE_GROUP) dựa trên postId.
     * Khi user được approve tham gia hoạt động, sẽ tạo room mới hoặc add member vào room cũ.
     */
    public ChatRoom getOrCreateActivityGroupChat(String postId, String postTitle, String ownerUsername) {
        String roomId = "room_activity_" + postId;
        ChatRoom existing = getRoomById(roomId);
        if (existing != null) return existing;

        // Also check by title for backwards compatibility
        for (ChatRoom room : chatRooms) {
            if (ChatRoom.TYPE_GROUP.equals(room.getType()) &&
                    room.getTitle() != null &&
                    room.getTitle().equalsIgnoreCase(postTitle)) {
                return room;
            }
        }

        String currentUser = FakePostRepository.getInstance().getCurrentUsername();
        List<String> members = new ArrayList<>();
        if (ownerUsername != null && !ownerUsername.isEmpty()) {
            members.add(ownerUsername);
        }
        if (currentUser != null && !members.contains(currentUser)) {
            members.add(currentUser);
        }

        ChatRoom newRoom = new ChatRoom(
                roomId,
                postTitle,
                ChatRoom.TYPE_GROUP,
                R.drawable.ic_user_placeholder,
                true,
                "",
                new ArrayList<>(),
                ownerUsername,
                members,
                new ArrayList<>()
        );
        chatRooms.add(0, newRoom);
        return newRoom;
    }

    /**
     * Add a member to an existing activity group chat.
     */
    public void addMemberToActivityChat(String postId, String username) {
        String roomId = "room_activity_" + postId;
        ChatRoom room = getRoomById(roomId);
        if (room != null) {
            room.addMember(username);
        }
    }

    private void seedRooms() {
        String currentUser = "Quỳnh Nguyễn";

        List<ChatMessage> coffeeMessages = new ArrayList<>();
        coffeeMessages.add(new ChatMessage("m1", "Minh Hoàng", "I can join around 7pm.", "09:20", false));
        coffeeMessages.add(new ChatMessage("m2", currentUser, "Great, I booked a table for 4.", "09:23", true));

        List<ChatMessage> codeMessages = new ArrayList<>();
        codeMessages.add(new ChatMessage("m3", "Lan Anh", "Let's split the UI tasks first.", "Yesterday", false));
        codeMessages.add(new ChatMessage("m4", currentUser, "I will handle the feed and profile flow.", "Yesterday", true));

        List<ChatMessage> directMessages = new ArrayList<>();
        directMessages.add(new ChatMessage("m5", "Minh Hoàng", "Do you want to review the mockup tonight?", "10:12", false));
        directMessages.add(new ChatMessage("m6", currentUser, "Yes, send it over and I will check it.", "10:15", true));

        // Activity group chat (post-based) - owner is the post creator
        chatRooms.add(new ChatRoom(
                "room_group_coffee",
                "Coffee Meetup",
                ChatRoom.TYPE_GROUP,
                R.drawable.ic_user_placeholder,
                true,
                "",
                coffeeMessages,
                currentUser,
                new ArrayList<>(Arrays.asList(currentUser, "Minh Hoàng")),
                new ArrayList<>(Arrays.asList("Lan Anh"))
        ));

        // Activity group chat - owner is someone else
        chatRooms.add(new ChatRoom(
                "room_group_code",
                "Design and Code Crew",
                ChatRoom.TYPE_GROUP,
                R.drawable.ic_user_placeholder,
                false,
                "Hoạt động 20 phút trước",
                codeMessages,
                "Lan Anh",
                new ArrayList<>(Arrays.asList("Lan Anh", currentUser)),
                new ArrayList<>()
        ));

        // Direct chat (friend DM)
        chatRooms.add(new ChatRoom(
                "room_direct_minh",
                "Minh Hoàng",
                ChatRoom.TYPE_DIRECT,
                R.drawable.ic_user_placeholder,
                true,
                "",
                directMessages,
                currentUser,
                new ArrayList<>(Arrays.asList(currentUser, "Minh Hoàng")),
                new ArrayList<>()
        ));
    }
}
