package com.nikitosh.spbau.frontier;

public interface DomainUrlsSet {
    boolean isEmpty();
    String popNextUrl();
    void addUrl(String url);
}
