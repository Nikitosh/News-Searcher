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
                if (i - SNIPPET_SIZE >= 0 && queryTermsSet.contains((wholeTerms.get(i - SNIPPET_SIZE)))) {
                    currentCount--;
                }
                if (queryTermsSet.contains((wholeTerms.get(i)))) {
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
        List<Integer> termsIndices = getTermsIndices(snippetText);
        System.out.println(termsIndices);
        for (int i = 0; i < termsIndices.size(); i++) {
            int startIndex = termsIndices.get(i);
            int endIndex = snippetText.length();
            if (i != termsIndices.size() - 1) {
                endIndex = termsIndices.get(i + 1);
            }
            for (String queryTerm : queryTermsSet) {
                if (snippetText.substring(startIndex, Math.min(endIndex, startIndex + queryTerm.length())).equals(queryTerm)) {
                    Collections.fill(toColor.subList(startIndex, endIndex), true);
                    break;
                }
            }
        }
        return toColor;
    }
}

