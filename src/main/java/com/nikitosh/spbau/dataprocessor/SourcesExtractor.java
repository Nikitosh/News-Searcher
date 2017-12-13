package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.parser.ParserHelper;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nikitosh.spbau.dataprocessor.ExtractorHelper.getBorderIndex;
import static com.nikitosh.spbau.dataprocessor.ExtractorHelper.getTermsIndices;
import static com.nikitosh.spbau.dataprocessor.ExtractorHelper.splitBySpaces;

public class SourcesExtractor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NOT_LETTERS = "[^а-яА-Я]";
    private static final String HREF_BEGINNING = "href=\"";
    private static final String SPACE = " ";
    private static final String BAD_DOMAINS_LIST_PATH = "src/main/resources/bad_domains_list.txt";
    private static final int MIN_ARTICLE_SIZE = 20;
    private static final int ARTICLE_INDENT = 15;
    private static final int MAX_SOURCES_NUMBER = 4;

    private static final Set<String> BAD_DOMAINS2;

    static {
        try {
            BAD_DOMAINS2 = new HashSet<>(Files.readAllLines(Paths.get(BAD_DOMAINS_LIST_PATH)));
        } catch (Exception exception) {
            LOGGER.error("Failed to create list of values due to exception: " + exception.getMessage() + "\n");
            throw new RuntimeException(exception);
        }
    }

    public static List<String> getSourcesList(String url, File file) {
        try {
            Document document = Jsoup.parse(file, "UTF-8", url);
            String wholeText = ParserHelper.getWholeDocument(document);
            String articleText = ArticleExtractor.INSTANCE.getText(wholeText);
            String cleanWholeText = wholeText.replaceAll(NOT_LETTERS, SPACE);
            List<String> wholeTerms = splitBySpaces(cleanWholeText);
            List<Integer> termsIndices = getTermsIndices(cleanWholeText);
            String cleanArticleText = articleText.replaceAll(NOT_LETTERS, SPACE);
            List<String> articleTerms = splitBySpaces(cleanArticleText);
            if (articleTerms.size() < MIN_ARTICLE_SIZE) {
                LOGGER.info("Article content has not enough russian words");
                return new ArrayList<>();
            }
            Map<String, Integer> articleTermsMap = new HashMap<>();
            for (String articleTerm : articleTerms) {
                articleTermsMap.put(articleTerm, articleTermsMap.getOrDefault(articleTerm, 0) + 1);
            }
            Collections.reverse(wholeTerms);
            int indexBegin = wholeTerms.size() - getBorderIndex(wholeTerms, articleTermsMap) - ARTICLE_INDENT;
            int positionBegin = termsIndices.get(indexBegin);
            Collections.reverse(wholeTerms);
            int indexEnd = indexBegin + getBorderIndex(wholeTerms.subList(indexBegin, wholeTerms.size()), articleTermsMap);
            int positionEnd = termsIndices.get(indexEnd);
            String foundPart = wholeText.substring(positionBegin, positionEnd);
            Pattern pattern = Pattern.compile(HREF_BEGINNING + "[^\"]*\"");
            Matcher matcher = pattern.matcher(foundPart);
            String mainDomain = ParserHelper.shortenDomainUrl(ParserHelper.getDomainName(url));
            List<String> realSourcesLinks = new ArrayList<>();
            while (matcher.find()) {
                String newGroup = matcher.group();
                String link = newGroup.substring(HREF_BEGINNING.length(), newGroup.length() - 1);
                try {
                    String linkDomain = ParserHelper.shortenDomainUrl(ParserHelper.getDomainName(link));
                    if (!linkDomain.contains(mainDomain) && !BAD_DOMAINS2.contains(linkDomain) &&
                            link.length() - (link.lastIndexOf(linkDomain) + linkDomain.length()) > 1) {
                        realSourcesLinks.add(link);
                    }
                } catch (Exception e) {
                    LOGGER.info("Couldn't shorten link's domain " + e.getMessage());
                }
            }
            if (realSourcesLinks.size() > MAX_SOURCES_NUMBER) {
                realSourcesLinks.clear();
            }
            return realSourcesLinks;
        } catch (Exception e) {
            LOGGER.error("Couldn't get sources list " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean isBadDomain(String domain) {
        return BAD_DOMAINS2.contains(domain);
    }
}
