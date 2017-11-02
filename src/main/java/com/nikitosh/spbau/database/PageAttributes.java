package com.nikitosh.spbau.database;

public class PageAttributes {
    private String url;
    private int wordCount;
    private int charactersCount;

    public PageAttributes(String url, int wordCount, int charactersCount) {
        this.url = url;
        this.wordCount = wordCount;
        this.charactersCount = charactersCount;
    }

    public String getUrl() {
        return url;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharactersCount() {
        return charactersCount;
    }
}
