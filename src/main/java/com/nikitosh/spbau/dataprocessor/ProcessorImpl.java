package com.nikitosh.spbau.dataprocessor;


import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProcessorImpl implements Processor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String REGEX = "[^[а-яА-Я]]";
    private static final String SPACE = " ";
    private static final String STOP_WORDS_FILE = "stop_words.txt";
    private static final String STOP_WORDS_ENCODING = "windows-1251";
    private static final String LINK_ENCODING = "utf-8";
    private static final Set<String> STOP_WORDS = new HashSet<>();

    @Override
    public List<String> getTermsFromFile(File file) {
        if (STOP_WORDS.isEmpty()) {
            fillStopWordsArray();
        }
        String rawContent = getArticleContent(file);
        List<String> rawWordsList = getWordsList(rawContent);
        List<String> rawWordsListWithoutStopWords = stopWordsRemoving(rawWordsList);
        return performStemming(rawWordsListWithoutStopWords);
    }

    private void fillStopWordsArray() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(STOP_WORDS_FILE), STOP_WORDS_ENCODING));
            String line;
            while ((line = br.readLine()) != null) {
                STOP_WORDS.add(line);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to fill stop words array: " + e.getMessage() + "\n");
        }
    }

    private String getArticleContent(File file) {
        StringBuilder allFile = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), LINK_ENCODING));
            String line;
            while ((line = br.readLine()) != null) {
                allFile.append(line);
            }
            return DefaultExtractor.INSTANCE.getText(allFile.toString());
        } catch (IOException | BoilerpipeProcessingException e) {
            LOGGER.error("Failed to get article content from file" + e.getMessage() + "\n");
            return null;
        }
    }

    private List<String> getWordsList(String content) {
        if (STOP_WORDS.isEmpty()) {
            fillStopWordsArray();
        }
        List<String> wordsList = new ArrayList<>();
        content = content.replaceAll(REGEX, SPACE);
        List<String> rawWordsList = Arrays.asList(content.split(SPACE));
        for (String el : rawWordsList) {
            if (el.length() > 0) {
                wordsList.add(el.toLowerCase());
            }
        }
        return wordsList;
    }

    private List<String> stopWordsRemoving(List<String> wordsList) {
        List<String> cleanList = new ArrayList<>();
        for (String word : wordsList) {
            if (!STOP_WORDS.contains(word)) {
                cleanList.add(word);
            }
        }
        return cleanList;
    }

    private List<String> performStemming(List<String> wordsList) {
        List<String> stemmedList = new ArrayList<>();
        for (String word : wordsList) {
            stemmedList.add(PorterStemming.stem(word));
        }
        return stemmedList;
    }
}
