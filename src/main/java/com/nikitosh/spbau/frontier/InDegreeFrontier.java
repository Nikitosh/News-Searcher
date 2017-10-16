package com.nikitosh.spbau.frontier;

public class InDegreeFrontier implements Frontier {
    @Override
    public DomainUrlsSet getNextDomainUrlsSet() {
        return null;
    }

    @Override
    public void addUrl(String url) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
