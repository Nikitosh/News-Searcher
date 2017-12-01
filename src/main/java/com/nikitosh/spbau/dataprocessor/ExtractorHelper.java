package com.nikitosh.spbau.dataprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtractorHelper {

    private static final String SPACE = " ";

    public static List<String> splitBySpaces(String text) {
        return Arrays.stream(text.split(SPACE))
                .filter(term -> !term.isEmpty())
                .collect(Collectors.toList());
    }

    public static List<Integer> getTermsIndices(String cleanText) {
        int index = 0;
        List<Integer> result = new ArrayList<>();
        cleanText += " ";
        while (index < cleanText.length() && index != -1) {
            while (index < cleanText.length() && cleanText.charAt(index) == ' ') {
                index++;
            }
            if (index < cleanText.length()) {
                result.add(index);
            }
            index = cleanText.indexOf(SPACE, index + 1);
        }
        return result;
    }

    public static int getBorderIndex(List<String> wholeTerms, Map<String, Integer> articleMap) {
        Map<String, Integer> currentMap = new HashMap<>();
        for (int i = 0; i < wholeTerms.size(); i++) {
            boolean filled = true;
            currentMap.put(wholeTerms.get(i), currentMap.getOrDefault(wholeTerms.get(i), 0) + 1);
            for (Map.Entry<String, Integer> entry : articleMap.entrySet()) {
                if (entry.getValue() > currentMap.getOrDefault(entry.getKey(), 0)) {
                    filled = false;
                    break;
                }
            }
            if (filled) {
                return i;
            }
        }
        return -1;
    }
}
