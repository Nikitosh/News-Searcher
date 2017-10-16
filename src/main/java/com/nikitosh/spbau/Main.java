package com.nikitosh.spbau;

import com.nikitosh.spbau.webcrawler.*;

public class Main {

    public static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl();
    }
}
