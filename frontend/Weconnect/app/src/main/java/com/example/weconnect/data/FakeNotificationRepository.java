package com.example.weconnect.data;

import java.util.ArrayList;
import java.util.List;

public class FakeNotificationRepository {

    public enum NotificationType {
        FRIEND_REQUEST_RECEIVED,  // Nhận lời mời kết bạn
        FRIEND_REQUEST_SENT,      // Đã gửi lời mời
        FRIEND_ACCEPTED,          // Đã chấp nhận kết bạn
        JOIN_REQUEST,             // Yêu cầu tham gia hoạt động
        JOIN_APPROVED,            // Đã được duyệt tham gia
        GENERAL                   // Thông báo chung
    }

    public static class NotificationItem {
        private final NotificationType type;
        private final String message;
        private final String relatedUsername;
        private final long timestamp;
        private boolean isRead;
        private boolean isActioned; // Đã xử lý (chấp nhận/từ chối)

        public NotificationItem(NotificationType type, String message, String relatedUsername, long timestamp) {
            this.type = type;
            this.message = message;
            this.relatedUsername = relatedUsername;
            this.timestamp = timestamp;
            this.isRead = false;
            this.isActioned = false;
        }

        public NotificationType getType() { return type; }
        public String getMessage() { return message; }
        public String getRelatedUsername() { return relatedUsername; }
        public long getTimestamp() { return timestamp; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        public boolean isActioned() { return isActioned; }
        public void setActioned(boolean actioned) { isActioned = actioned; }
    }

    private static FakeNotificationRepository instance;
    private final List<NotificationItem> notifications = new ArrayList<>();

    private FakeNotificationRepository() {
        seed();
    }

    public static synchronized FakeNotificationRepository getInstance() {
        if (instance == null) {
            instance = new FakeNotificationRepository();
        }
        return instance;
    }

    public List<NotificationItem> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public void addNotification(NotificationItem item) {
        notifications.add(0, item);
    }

    public int getUnreadCount() {
        int count = 0;
        for (NotificationItem item : notifications) {
            if (!item.isRead()) count++;
        }
        return count;
    }

    private void seed() {
        long now = System.currentTimeMillis();
        long oneHour = 60L * 60L * 1000L;
        long oneDay = 24L * oneHour;

        // Hôm nay
        notifications.add(new NotificationItem(
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Thu Hương đã gửi lời mời kết bạn đến bạn",
                "Thu Hương",
                now - 2 * oneHour
        ));

        notifications.add(new NotificationItem(
                NotificationType.JOIN_REQUEST,
                "Đức Anh muốn tham gia hoạt động \"Đá bóng cuối tuần\" của bạn",
                "Đức Anh",
                now - 4 * oneHour
        ));

        // Hôm qua
        notifications.add(new NotificationItem(
                NotificationType.FRIEND_ACCEPTED,
                "Minh Hoàng đã chấp nhận lời mời kết bạn của bạn",
                "Minh Hoàng",
                now - oneDay - 3 * oneHour
        ));

        notifications.add(new NotificationItem(
                NotificationType.JOIN_APPROVED,
                "Bạn đã được duyệt tham gia hoạt động \"Cà phê sáng\"",
                "Lan Anh",
                now - oneDay - 6 * oneHour
        ));

        // 2 ngày trước
        notifications.add(new NotificationItem(
                NotificationType.GENERAL,
                "Chào mừng bạn đến với WeConnect! Hãy tìm bạn mới ngay nào 🎉",
                "",
                now - 2 * oneDay
        ));
    }
}
