package com.nikitosh.spbau.dataprocessor;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;

public class TitleExtractor {
    public static String getTitle(String url, File file) {
        try {
            return Jsoup.parse(file, "UTF-8", url).title();
        } catch (IOException exception) {
            return "";
        }
    }
}
