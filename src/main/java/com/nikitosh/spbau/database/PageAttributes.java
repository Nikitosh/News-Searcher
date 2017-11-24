package com.nikitosh.spbau.database;

public class PageAttributes {
    private int id;
    private String url;
    private int wordCount;
    private int charactersCount;
    private double length;

    public PageAttributes(int id, String url, int wordCount, int charactersCount, double length) {
        this.id = id;
        this.url = url;
        this.wordCount = wordCount;
        this.charactersCount = charactersCount;
        this.length = length;
    }

    public int getId() {
        return id;
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

    public double getLength() {
        return length;
    }
}
