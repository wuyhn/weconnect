package com.example.weconnect.models;

/**
 * Post response từ backend API.
 * Map với PostResponse DTO của Spring Boot.
 */
public class PostResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private String content;
    private String interestTag;
    private String location;
    private String imageUrl;
    private int maxMembers;
    private int memberCount;
    private int likesCount;
    private int commentsCount;
    private boolean joined;
    private boolean pendingApproval;
    private boolean archived;
    private boolean expired;
    private String startTime;
    private String endTime;
    private String createdAt;

    public Long getId() { return id; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public String getInterestTag() { return interestTag; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public int getMaxMembers() { return maxMembers; }
    public int getMemberCount() { return memberCount; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public boolean isJoined() { return joined; }
    public boolean isPendingApproval() { return pendingApproval; }
    public boolean isArchived() { return archived; }
    public boolean isExpired() { return expired; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getCreatedAt() { return createdAt; }

    /**
     * Convert thành Post model cũ để tương thích với PostAdapter hiện tại
     */
    public Post toPost() {
        // Parse endTime string (ISO) to millis
        long startMillis = System.currentTimeMillis();
        long endMillis = System.currentTimeMillis() + 24L * 60L * 60L * 1000L;
        java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
        try {
            if (startTime != null && !startTime.isEmpty()) {
                startMillis = isoFormat.parse(startTime).getTime();
            }
            if (endTime != null && !endTime.isEmpty()) {
                endMillis = isoFormat.parse(endTime).getTime();
            }
        } catch (Exception e) {
            // fallback to defaults
        }

        Post post = new Post(
                String.valueOf(id),
                authorName != null ? authorName : "",
                createdAt != null ? createdAt : "",
                content != null ? content : "",
                interestTag != null ? interestTag : "",
                location != null ? location : "",
                com.example.weconnect.R.drawable.ic_user_placeholder,
                0,
                memberCount,
                likesCount,
                commentsCount,
                maxMembers,
                joined,
                startMillis,
                endMillis,
                archived || expired
        );
        post.setPendingApproval(pendingApproval);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            post.setPostImageUri(imageUrl);
        }
        if (authorId != null) {
            post.setAuthorId(authorId);
        }
        return post;
    }
}
