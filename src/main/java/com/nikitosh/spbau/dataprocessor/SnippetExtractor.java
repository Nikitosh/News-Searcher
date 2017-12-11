package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.parser.ParserHelper;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.nikitosh.spbau.dataprocessor.ExtractorHelper.getTermsIndices;
import static com.nikitosh.spbau.dataprocessor.ExtractorHelper.splitBySpaces;

public final class SnippetExtractor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NOT_LETTERS = "[^а-яА-Я]";
    private static final String SPACE = " ";
    private static final String ELLIPSIS = "...";
    private static final int SNIPPET_SIZE = 20;
    private static final Processor processor = new ProcessorImpl();

    public static String getSnippet(Document document, Set<String> queryTermsSet) {
        try {
            String allDocument = ParserHelper.getWholeDocument(document);
            String wholeText = DefaultExtractor.INSTANCE.getText(allDocument);
            String cleanWholeText = wholeText.replaceAll(NOT_LETTERS, SPACE);
            List<String> wholeTerms = splitBySpaces(cleanWholeText);
            List<Integer> termsIndices = getTermsIndices(cleanWholeText);
            int currentCount = 0;
            int maxCount = 0;
            int resultIndex = SNIPPET_SIZE;
            for (int i = 0; i < wholeTerms.size(); i++) {
                if (i - SNIPPET_SIZE >= 0 && queryTermsSet.contains(processor.getTermsFromString(wholeTerms.get(i - SNIPPET_SIZE)).get(0))) {
                    currentCount--;
                }
                if (queryTermsSet.contains(processor.getTermsFromString(wholeTerms.get(i)).get(0))) {
                    currentCount++;
                }
                if (currentCount > maxCount && i >= SNIPPET_SIZE) {
                    maxCount = currentCount;
                    resultIndex = i;
                }
            }
            String resultString = wholeText.substring(
                    termsIndices.get(resultIndex - SNIPPET_SIZE), termsIndices.get(resultIndex + 1));
            resultString = resultString.replaceAll("\n", " ");
            return ELLIPSIS + ParserHelper.replaceBadSymbols(resultString) + ELLIPSIS;
        } catch (Exception e) {
            LOGGER.error("Couldn't make snippet " + e.getMessage());
        }
        return "";
    }

    public static List<Boolean> colorSnippet(String snippetText, Set<String> queryTermsSet) {
        List<Boolean> toColor = Arrays.asList(new Boolean[snippetText.length()]);
        Collections.fill(toColor, false);
        String cleanSnippetText = snippetText.replaceAll(NOT_LETTERS, SPACE);
        List<String> wholeTerms = splitBySpaces(cleanSnippetText);
        List<Integer> termsIndices = getTermsIndices(cleanSnippetText);
        for (int i = 0; i < termsIndices.size(); i++) {
            int startIndex = termsIndices.get(i);
            for (String queryTerm : queryTermsSet) {
                String processedText = "";
                try {
                    processedText = processor.getTermsFromString(wholeTerms.get(i)).get(0);
                } catch (Exception e) {
                    LOGGER.info("Empty result of processing " + e.getMessage());
                }
                if (processedText.equals(queryTerm)) {
                    int endIndex = cleanSnippetText.indexOf(SPACE, startIndex);
                    if (endIndex == -1) {
                        endIndex = cleanSnippetText.length();
                    }
                    Collections.fill(toColor.subList(startIndex, endIndex), true);
                    break;
                }
            }
        }
        return toColor;
    }
}

