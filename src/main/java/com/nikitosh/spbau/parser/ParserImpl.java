package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.UrlInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nikitosh.spbau.parser.ParserHelper.*;

public class ParserImpl implements Parser {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public UrlInfo parse(String url) {

        String text = "";
        List<String> links = new ArrayList<>();

        try {
            Document document = getDocument(url);
            text = getWholeDocument(document);
            links = getLinks(document, url);

        } catch (IOException e) {
            LOGGER.error("Failed to get document from url: " + url + " due to exception: " + e.getMessage()
                    + "\n");
        }

        return new UrlInfo(text, links);
    }
}

