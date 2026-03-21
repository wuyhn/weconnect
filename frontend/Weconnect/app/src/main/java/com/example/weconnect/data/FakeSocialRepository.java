package com.example.weconnect.data;

import java.util.HashMap;
import java.util.Map;

public class FakeSocialRepository {

    public static class SocialState {
        private final boolean selfProfile;
        private boolean friend;
        private boolean blocked;

        public SocialState(boolean selfProfile, boolean friend, boolean blocked) {
            this.selfProfile = selfProfile;
            this.friend = friend;
            this.blocked = blocked;
        }

        public boolean isSelfProfile() {
            return selfProfile;
        }

        public boolean isFriend() {
            return friend;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setFriend(boolean friend) {
            this.friend = friend;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }
    }

    private static FakeSocialRepository instance;

    private final Map<String, SocialState> stateMap = new HashMap<>();
    private final String currentUsername = "Quynh Nguyen";

    private FakeSocialRepository() {
        seed();
    }

    public static synchronized FakeSocialRepository getInstance() {
        if (instance == null) {
            instance = new FakeSocialRepository();
        }
        return instance;
    }

    public SocialState getState(String username) {
        SocialState state = stateMap.get(username);
        if (state != null) {
            return state;
        }

        boolean self = currentUsername.equalsIgnoreCase(username);
        SocialState fallback = new SocialState(self, false, false);
        stateMap.put(username, fallback);
        return fallback;
    }

    public void toggleFriend(String username) {
        SocialState state = getState(username);
        if (state.isSelfProfile() || state.isBlocked()) {
            return;
        }
        state.setFriend(!state.isFriend());
    }

    public void toggleBlocked(String username) {
        SocialState state = getState(username);
        if (state.isSelfProfile()) {
            return;
        }
        boolean blocked = !state.isBlocked();
        state.setBlocked(blocked);
        if (blocked) {
            state.setFriend(false);
        }
    }

    private void seed() {
        stateMap.put(currentUsername, new SocialState(true, false, false));
        stateMap.put("Minh Hoang", new SocialState(false, true, false));
        stateMap.put("Lan Anh", new SocialState(false, false, false));
        stateMap.put("Hai Dang", new SocialState(false, false, true));
    }
}
