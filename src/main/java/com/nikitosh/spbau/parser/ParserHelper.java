package com.nikitosh.spbau.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String CRAWLER_BOT = "CrawlerBot";
    public static final int TIMEOUT = 2000;
    private static final String A_HREF = "a[href]";
    private static final String HREF = "href";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String WWW = "www.";
    private static final String HTTP_HEADER = "http://";

    private ParserHelper() {
    }

    public static String getDomainName(String url) throws URISyntaxException {
        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = HTTP_HEADER + url;
        }
        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null) {
            return "";
        }
        return domain.startsWith(WWW) ? domain.substring(WWW.length()) : domain;
    }

    public static String shortenDomainURL(String domainURL) {
        String[] parts = domainURL.split("\\.");
        try {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        } catch (Exception e) {
            LOGGER.info("Bad domain URL passed " + e.getMessage());
            return "";
        }
    }


    public static boolean isValidUrl(String url) {
        try {
            new URI(url).getHost();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String getWholeText(Document document) {
        return document.body().text();
    }

    public static String getWholeDocument(Document document) {
        return document.toString();
    }

    public static Document getDocument(String url) throws IOException {
        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = HTTP_HEADER + url;
        }
        Connection connection = Jsoup.connect(url).userAgent(CRAWLER_BOT).timeout(TIMEOUT).validateTLSCertificates(false);
        if (connection == null) {
            return null;
        }
        return connection.get();
    }

    public static List<String> getLinks(Document document, String url) {
        List<String> links = new ArrayList<>();
        Elements elements = document.select(A_HREF);
        for (Element element : elements) {
            try {
                String link = getDomainName(url);
                String ending = element.attr(HREF);
                if (ending.length() > 0 && ending.charAt(0) == '/' && !(ending.length() >= 2 && ending.charAt(1) == '/')) {
                    link += ending;
                } else {
                    if ((ending.length() >= HTTP.length() && ending.substring(0, 4).equals(HTTP))) {
                        link = ending;
                    } else {
                        continue;
                    }
                }
                if (!link.substring(0, 4).equals(HTTP)) {
                    link = HTTP_HEADER + link;
                }
                links.add(link);
            } catch (URISyntaxException exception) {
                LOGGER.error("Failed to get domain name from url: " + url + " due to exception: "
                        + exception.getMessage() + "\n");
            }
        }
        return links;
    }
}
