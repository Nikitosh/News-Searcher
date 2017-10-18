package com.nikitosh.spbau.storage;

import java.time.LocalDateTime;
import java.util.List;

public class UrlInfo {
    private String text;
    private List<String> links;

    public UrlInfo(String text, List<String> links) {
        this.text = text;
        this.links = links;
    }

    public String getText() {
        return text;
    }

    public List<String> getLinks() {
        return links;
    }
}
