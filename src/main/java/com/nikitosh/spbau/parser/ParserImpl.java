package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParserImpl implements Parser {
    @Override
    public UrlInfo parse(String url) {
        return null;
    }

    static String getWholeText(Document document) {
        return document.body().text();
    }

    static Document getDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT);
        return connection.get();
    }
}
