package com.nikitosh.spbau.frontier;

public interface Frontier {
    DomainUrlsSet getNextDomainUrlsSet();
    void addUrl(String url);
    boolean isFinished();
}
