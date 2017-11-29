package com.nikitosh.spbau.dataprocessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeExtractor {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static Date parseDate(String string) {
        try {
            return DATE_FORMAT.parse(string);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "null";
        }
        return DATE_FORMAT.format(date);
    }

    public static Date getTime(String url, File file) {
        try {
            Document document = Jsoup.parse(file, "UTF-8", url);
            return new Date();
        } catch (IOException exception) {
            return null;
        }
    }
}
