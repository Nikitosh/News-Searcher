package com.nikitosh.spbau.parser;

import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.*;
import java.net.*;

public final class ParserHelper {
    public static final String CRAWLER_BOT = "CrawlerBot";
    public static final int TIMEOUT = 2000;
    private static final String WWW = "www.";

    private ParserHelper() {}

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith(WWW) ? domain.substring(WWW.length()) : domain;
    }

    public static String getWholeText(Document document) {
        return document.body().text();
    }

    public static Document getDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent(CRAWLER_BOT).timeout(TIMEOUT);
        return connection.get();
    }
}
