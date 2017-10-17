package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.*;

import java.net.URI;
import java.net.URISyntaxException;


public interface Parser {

    String USER_AGENT = "CrawlerBot";
    int TIMEOUT = 2000;

    static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    UrlInfo parse(String url);

}
