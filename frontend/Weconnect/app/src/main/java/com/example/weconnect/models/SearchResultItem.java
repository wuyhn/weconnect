package com.example.weconnect.models;

public class SearchResultItem {

    public static final int TYPE_SECTION = 0;
    public static final int TYPE_USER = 1;
    public static final int TYPE_POST = 2;

    private int viewType;
    private String title;
    private String subtitle;
    private int avatarResId;

    private String username;
    private String content;
    private String tag;
    private String location;
    private int memberCount;
    private int maxMembers;

    public SearchResultItem(int viewType, String title, String subtitle, int avatarResId) {
        this.viewType = viewType;
        this.title = title;
        this.subtitle = subtitle;
        this.avatarResId = avatarResId;
    }

    public SearchResultItem(int viewType, String title, String subtitle, int avatarResId,
                            String username, String content, String tag, String location,
                            int memberCount, int maxMembers) {
        this.viewType = viewType;
        this.title = title;
        this.subtitle = subtitle;
        this.avatarResId = avatarResId;
        this.username = username;
        this.content = content;
        this.tag = tag;
        this.location = location;
        this.memberCount = memberCount;
        this.maxMembers = maxMembers;
    }

    public int getViewType() {
        return viewType;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public String getLocation() {
        return location;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getMaxMembers() {
        return maxMembers;
    }
}