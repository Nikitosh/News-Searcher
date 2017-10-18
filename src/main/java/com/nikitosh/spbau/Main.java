package com.nikitosh.spbau;

import com.nikitosh.spbau.webcrawler.WebCrawler;
import com.nikitosh.spbau.webcrawler.WebCrawlerImpl;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl();
    }
}
