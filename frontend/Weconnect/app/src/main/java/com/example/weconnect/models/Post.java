package com.example.weconnect.models;

public class Post {
    private String id;
    private String username;
    private String timeAgo;
    private String content;
    private String interestTag;
    private int avatarResId;
    private int imageResId;
    private int memberCount;
    private int likesCount;
    private int commentsCount;
    private int maxMembers;
    private boolean joined;

    // Constructor
    public Post(String id, String username, String timeAgo, String content,String interestTag,
                int avatarResId, int imageResId, int memberCount, int likesCount,
                int commentsCount, int maxMembers, boolean joined) {
        this.id = id;
        this.username = username;
        this.timeAgo = timeAgo;
        this.content = content;
        this.interestTag = interestTag;
        this.avatarResId = avatarResId;
        this.imageResId = imageResId;
        this.memberCount = memberCount;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.maxMembers = maxMembers;
        this.joined = joined;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getTimeAgo() { return timeAgo; }
    public String getContent() { return content; }
    public String getInterestTag() { return interestTag; }
    public int getAvatarResId() { return avatarResId; }
    public int getImageResId() { return imageResId; }
    public int getMemberCount() { return memberCount; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public int getMaxMembers() { return maxMembers; }
    public boolean isJoined() { return joined; }

    // Setters
    public void setJoined(boolean joined) { this.joined = joined; }
}