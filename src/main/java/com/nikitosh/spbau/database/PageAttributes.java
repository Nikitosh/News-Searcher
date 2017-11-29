package com.nikitosh.spbau.database;

import java.util.Date;

public class PageAttributes {
    private int id;
    private String fileName;
    private int wordCount;
    private int charactersCount;
    private Date time;
    private String title;
    private double length;

    public PageAttributes(int id, String fileName, int wordCount, int charactersCount, Date time, String title, double length) {
        this.id = id;
        this.fileName = fileName;
        this.wordCount = wordCount;
        this.charactersCount = charactersCount;
        this.time = time;
        this.title = title;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getCharactersCount() {
        return charactersCount;
    }

    public Date getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public double getLength() {
        return length;
    }
}
