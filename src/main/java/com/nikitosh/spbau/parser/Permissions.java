package com.nikitosh.spbau.parser;

import java.util.*;

public class Permissions {
    private List<String> allowedUrlMasks;
    private List<String> disallowedUrlMasks;
    private int delayInSeconds;
    public Permissions(List<String> allowedUrlMasks, List<String> disallowedUrlMasks, int delayInSeconds) {
        this.allowedUrlMasks = allowedUrlMasks;
        this.disallowedUrlMasks = disallowedUrlMasks;
        this.delayInSeconds = delayInSeconds;
    }
}
