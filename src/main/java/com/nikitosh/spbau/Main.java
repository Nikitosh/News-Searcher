package com.nikitosh.spbau;

import com.nikitosh.spbau.webcrawler.*;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl();
    }
}
