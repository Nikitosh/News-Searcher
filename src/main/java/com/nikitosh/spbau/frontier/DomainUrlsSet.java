package com.nikitosh.spbau.frontier;

public interface DomainUrlsSet {
    String popNextUrl();
    void addUrl(String url);
}
