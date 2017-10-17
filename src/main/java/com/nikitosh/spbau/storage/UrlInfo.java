package com.nikitosh.spbau.storage;

import java.time.*;
import java.util.*;

public class UrlInfo {
    private String text;
    private LocalDateTime time;
    private List<String> links;

    public UrlInfo(String text, LocalDateTime time, List<String> links) {
        this.text = text;
        this.time = time;
        this.links = links;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public List<String> getLinks() {
        return links;
    }
}
