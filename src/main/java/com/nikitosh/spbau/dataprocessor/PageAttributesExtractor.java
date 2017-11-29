package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.database.PageAttributes;

import java.util.Date;
import java.util.List;

public class PageAttributesExtractor {
    public static PageAttributes getPageAttributes(
            int id,
            String fileName,
            List<String> terms,
            Date time,
            String title,
            double length) {
        return new PageAttributes(
                id,
                fileName,
                terms.size(),
                terms.stream().mapToInt(String::length).sum(),
                time,
                title,
                length);
    }
}
