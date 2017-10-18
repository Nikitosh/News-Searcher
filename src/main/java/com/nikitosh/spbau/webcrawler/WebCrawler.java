package com.nikitosh.spbau.webcrawler;

import java.util.List;

public interface WebCrawler {
    void crawl(List<String> seedUrls);
}
