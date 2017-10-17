package com.nikitosh.spbau.parser;

public class Permissions {
    private boolean isIndexingAllowed;
    private boolean isFollowingAllowed;

    public Permissions(boolean isIndexingAllowed, boolean isFollowingAllowed) {
        this.isIndexingAllowed = isIndexingAllowed;
        this.isFollowingAllowed = isFollowingAllowed;
    }

    public boolean isIndexingAllowed() {
        return isIndexingAllowed;
    }

    public boolean isFollowingAllowed() {
        return isFollowingAllowed;
    }
}
