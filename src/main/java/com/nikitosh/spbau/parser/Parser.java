package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.net.*;

public interface Parser {

    String USER_AGENT = "CrawlerBot";
    int TIMEOUT = 2000;

    static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    UrlInfo parse(String url);


    static String getWholeText(Document document) {
        return document.body().text();
    }

    static Document getDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT);
        return connection.get();
    }

}
