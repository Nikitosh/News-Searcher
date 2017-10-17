package com.nikitosh.spbau.parser;

import java.util.*;

public class RobotsTxtPermissions {
    private List<String> allowedUrlMasks;
    private List<String> disallowedUrlMasks;
    private int delayInSeconds;

    public RobotsTxtPermissions(List<String> allowedUrlMasks, List<String> disallowedUrlMasks, int delayInSeconds) {
        this.allowedUrlMasks = allowedUrlMasks;
        this.disallowedUrlMasks = disallowedUrlMasks;
        this.delayInSeconds = delayInSeconds;
    }

    public List<String> getAllowedUrlMasks() {
        return this.allowedUrlMasks;
    }

    public List<String> getDisallowedUrlMasks() {
        return this.disallowedUrlMasks;
    }

    public int getDelayInSeconds() {
        return this.delayInSeconds;
    }
}
