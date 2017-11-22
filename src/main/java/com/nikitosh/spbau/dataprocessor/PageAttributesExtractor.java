package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.database.PageAttributes;

import java.util.List;

public class PageAttributesExtractor {
    public static PageAttributes getPageAttributes(int id, String url, List<String> terms) {
        return new PageAttributes(id, url, terms.size(), terms.stream().mapToInt(String::length).sum());
    }
}
