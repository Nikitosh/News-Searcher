package com.nikitosh.spbau;

import com.nikitosh.spbau.webcrawler.WebCrawler;
import com.nikitosh.spbau.webcrawler.WebCrawlerImpl;

import java.io.IOException;
import java.util.Arrays;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws IOException {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl(Arrays.asList("https://news.yandex.ru/"));
    }
}
