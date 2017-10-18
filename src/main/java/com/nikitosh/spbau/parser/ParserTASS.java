package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.UrlInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParserTASS implements Parser {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String META_NAME = "meta[name]";
    private static final String NAME = "name";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String CONTENT = "content";
    private static final String DIV_CLASS = "div[class]";
    private static final String CLASS = "class";
    private static final String MATERIAL_TEXT = "b-material-text__l js-mediator-article";
    private static final String ALSO_LOOK = "Смотрите также";

    @Override
    public UrlInfo parse(String url) {
        String text = "";
        LocalDateTime time = LocalDateTime.MIN;
        List<String> links = new ArrayList<>();

        try {
            Document document = ParserHelper.getDocument(url);
            text = ParserHelper.getWholeDocument(document);
            time = getTime(document);
            links = ParserHelper.getLinks(document, url);

        } catch (IOException e) {
            LOGGER.error("Failed to get document from url: " + url + " due to exception: " + e.getMessage()
                    + "\n");

        }

        return new UrlInfo(text, links);
    }

    private LocalDateTime getTime(Document document) {
        LocalDateTime time = LocalDateTime.MIN;
        Elements info = document.select(META_NAME);
        for (Element el : info) {
            if (el.attr(NAME).toLowerCase().equals(PUBLISH_DATE)) {
                String content = el.attr(CONTENT);
                String[] dateParts = content.split(" |-|:");
                int[] date = new int[dateParts.length];
                for (int i = 0; i < dateParts.length; i++) {
                    date[i] = Integer.valueOf(dateParts[i]);
                }
                time = LocalDateTime.of(date[0], date[1], date[2], date[3], date[4], date[5]);
            }
        }
        return time;
    }

    private String getText(Document document) {
        Elements elements = document.select(DIV_CLASS);
        for (Element el : elements) {
            if (el.attr(CLASS).toLowerCase().equals(MATERIAL_TEXT)) {
                String result = el.text();
                result = result.substring(0, result.indexOf(ALSO_LOOK));
                return result;
            }
        }
        return "";
    }
}
