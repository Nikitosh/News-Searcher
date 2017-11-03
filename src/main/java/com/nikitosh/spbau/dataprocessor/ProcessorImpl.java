package com.nikitosh.spbau.dataprocessor;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProcessorImpl implements Processor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NOT_LETTERS = "[^[а-яА-Я]]";
    private static final String SPACE = " ";
    private static final String STOP_TERMS_PATH = "src/main/resources/stop_terms.txt";
    private static final Charset STOP_TERMS_ENCODING = Charset.forName("windows-1251");
    private static final Charset SAVED_PAGE_ENCODING = Charset.forName("utf-8");

    @Override
    public List<String> getTermsFromFile(File file) {
        try {
            Set<String> stopTerms = new HashSet<>(Files.readAllLines(Paths.get(STOP_TERMS_PATH), STOP_TERMS_ENCODING));
            String rawContent = getArticleContent(file).replaceAll(NOT_LETTERS, SPACE);
            return Arrays.stream(rawContent.split(SPACE))
                    .filter(term -> !term.isEmpty())
                    .filter(term -> !stopTerms.contains(term))
                    .map(PorterStemming::stem)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            LOGGER.error("Failed to fill stop terms list due to exception: " + exception.getMessage() + "\n");
            return Collections.emptyList();
        }
    }

    private String getArticleContent(File file) {
        try {
            String content = String.join("", Files.readAllLines(file.toPath(), SAVED_PAGE_ENCODING));
            return DefaultExtractor.INSTANCE.getText(content);
        } catch (IOException | BoilerpipeProcessingException exception) {
            LOGGER.error("Failed to get article content from file due to exception: " + exception.getMessage()
                    + "\n");
            return "";
        }
    }
}
