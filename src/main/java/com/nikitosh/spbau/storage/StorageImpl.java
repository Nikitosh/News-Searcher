package com.nikitosh.spbau.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class StorageImpl implements Storage {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String STORAGE_DIRECTORY = "../data";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int MIN_CHARACTERS_NUMBER = 50;

    public StorageImpl() {
        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @Override
    public void addDocument(String url, UrlInfo urlInfo) {
        if (urlInfo.getText() == null || urlInfo.getText().length() < MIN_CHARACTERS_NUMBER) {
            return;
        }
        url = url
                .replace("http://", "")
                .replace("https://", "")
                .replace("www.", "");
        url = url.replaceAll("[^a-zA-Z0-9.-]", "_");
        Path path = Paths.get(STORAGE_DIRECTORY, url);
        if (!(path.toFile()).exists()) {
            List<String> data = Arrays.asList(urlInfo.getText());
            try {
                Files.write(path, data, UTF_8);
            } catch (IOException exception) {
                LOGGER.error("Failed to index document with url: " + url + " due to exception: "
                        + exception.getMessage() + "\n");
            }
        }
    }
}
