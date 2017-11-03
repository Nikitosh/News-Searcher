package com.nikitosh.spbau;

import com.nikitosh.spbau.dataprocessor.DataHandler;
import com.nikitosh.spbau.dataprocessor.DataHandlerImpl;
import com.nikitosh.spbau.webcrawler.WebCrawler;
import com.nikitosh.spbau.webcrawler.WebCrawlerImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.nikitosh.spbau.storage.StorageImpl.STORAGE_DIRECTORY_PATH;

public final class Main {
    private static final List<String> SEED_URLS = Arrays.asList(
            "http://news.yandex.ru/",
            "http://news.google.ru/",
            "http://news.mail.ru/"
    );

    private Main() {}

    public static void main(String[] args) throws IOException {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl(SEED_URLS);
        DataHandler dataHandler = new DataHandlerImpl();
        dataHandler.process(STORAGE_DIRECTORY_PATH);
    }
}
