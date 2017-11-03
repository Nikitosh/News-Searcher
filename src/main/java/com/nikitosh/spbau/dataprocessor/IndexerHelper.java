package com.nikitosh.spbau.dataprocessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IndexerHelper {
    public static Map<String, DictionaryEntry> getInvertedIndex(int documentId, List<String> terms) {
        return IntStream.range(0, terms.size())
                .boxed()
                .collect(Collectors.groupingBy(terms::get, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new DictionaryEntry(documentId, entry.getValue())));
    }
}
