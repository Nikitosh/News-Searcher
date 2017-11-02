package com.nikitosh.spbau.frontier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class InDegreeDomainUrlsSet implements DomainUrlsSet {
    private static final int MAX_SIZE = 500;
    private static final int WORD_BONUS = 2;
    private static final List<String> KEY_LINK_WORDS = Arrays.asList("news", "article", "event", "material", "novost");

    private Map<String, Integer> urlReferencesNumber = new HashMap<>();
    private TreeSet<UrlReferences> urlReferencesSet = new TreeSet<>();

    private static class UrlReferences implements Comparable<UrlReferences> {
        private static final int PRIME = 31;

        private String url;
        private int referencesNumber;

        UrlReferences(String url, int referencesNumber) {
            this.url = url;
            this.referencesNumber = referencesNumber;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof UrlReferences)) {
                return false;
            }
            UrlReferences urlReferences = (UrlReferences) other;
            return url.equals(urlReferences.url) && referencesNumber == urlReferences.referencesNumber;
        }

        @Override
        public int hashCode() {
            return url.hashCode() * PRIME + referencesNumber;
        }

        @Override
        public int compareTo(UrlReferences urlReferences) {
            return referencesNumber != urlReferences.referencesNumber
                    ? Integer.compare(referencesNumber, urlReferences.referencesNumber)
                    : url.compareTo(urlReferences.url);
        }
    }

    @Override
    public boolean isEmpty() {
        return urlReferencesSet.isEmpty();
    }

    @Override
    public String popNextUrl() {
        UrlReferences urlReferences = urlReferencesSet.last();
        urlReferencesSet.remove(urlReferences);
        urlReferencesNumber.remove(urlReferences.url);
        return urlReferences.url;
    }

    @Override
    public void addUrl(String url) {
        int priority = 1;
        if (!urlReferencesNumber.containsKey(url) && urlReferencesNumber.size() < MAX_SIZE) {
            for (String word : KEY_LINK_WORDS) {
                if (url.contains(word)) {
                    priority += WORD_BONUS;
                }
            }
            updateUrl(url, priority);
        } else {
            int referencesNumber = urlReferencesNumber.get(url);
            urlReferencesSet.remove(new UrlReferences(url, referencesNumber));
            updateUrl(url, referencesNumber + priority);
        }
    }

    private void updateUrl(String url, int referencesNumber) {
        urlReferencesNumber.put(url, referencesNumber);
        urlReferencesSet.add(new UrlReferences(url, referencesNumber));
    }
}
