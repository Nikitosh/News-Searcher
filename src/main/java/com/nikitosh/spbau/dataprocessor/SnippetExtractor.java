package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.parser.ParserHelper;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
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


    private static List<String> formatSnippet(String snippetText, int maxSnippetWidth) {
        String cleanWholeText = snippetText.replaceAll(NOT_LETTERS, SPACE);
        List<Integer> termsIndices = getTermsIndices(cleanWholeText);
        List<String> resultList = new ArrayList<>();
        int currentStart = 0;
        int currentLength = termsIndices.get(0);
        for (int i = 1; i < termsIndices.size(); i++) {
            if (currentLength + termsIndices.get(i) - termsIndices.get(i - 1) > maxSnippetWidth) {
                currentLength = termsIndices.get(i) - termsIndices.get(i - 1);
                resultList.add(snippetText.substring(currentStart, termsIndices.get(i - 1)));
                currentStart = termsIndices.get(i - 1);
            } else {
                currentLength += termsIndices.get(i) - termsIndices.get(i - 1);
            }
        }
        resultList.add(snippetText.substring(currentStart, snippetText.length()));
        return resultList;

    }

    private static List<String> getSnippet(Document document, Set<String> queryTermsSet, int maxSnippetWidth) {
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
                if (currentCount > maxCount && i > SNIPPET_SIZE) {
                    maxCount = currentCount;
                    resultIndex = i;
                }
            }
            String resultString = wholeText.substring(termsIndices.get(resultIndex - SNIPPET_SIZE), termsIndices.get(resultIndex + 1));
            resultString = resultString.replaceAll("\n", " ");
            return formatSnippet(ELLIPSIS + resultString + ELLIPSIS, maxSnippetWidth);
        } catch (Exception e) {
            LOGGER.error("Couldn't make snippet " + e.getMessage());
        }
        return new ArrayList<>();
    }

}

