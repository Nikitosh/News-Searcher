package com.nikitosh.spbau.parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public final class ParserHelper {
    public static final String CRAWLER_BOT = "CrawlerBot";
    public static final int TIMEOUT = 2000;
    private static final String A_HREF = "a[href]";
    private static final String HREF = "href";
    private static final String HTTP = "http";
    private static final String WWW = "www.";

    private ParserHelper() {
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith(WWW) ? domain.substring(WWW.length()) : domain;
    }

    public static String getWholeText(Document document) {
        return document.body().text();
    }

    public static String getWholeDocument(Document document) {
        return document.toString();
    }

    public static Document getDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent(CRAWLER_BOT).timeout(TIMEOUT);
        return connection.get();
    }

    public static List<String> getLinks(Document document, String url) {
        List<String> res = new ArrayList<>();
        Elements links = document.select(A_HREF);
        for (Element el : links) {
            String newLink = url;
            String ending = el.attr(HREF);
            if (ending.length() > 0 && ending.charAt(0) == '/') {
                newLink += ending;
            } else {
                if ((ending.length() >= 4 && ending.substring(0, 4).equals(HTTP))) {
                    newLink = ending;
                } else {
                    continue;
                }
            }
            res.add(newLink);
        }
        return res;
    }
}
