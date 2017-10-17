package com.nikitosh.spbau.storage;

import org.apache.logging.log4j.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class StorageImpl implements Storage {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String STORAGE_DIRECTORY = "data";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public StorageImpl() {
        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @Override
    public void addDocument(String url, UrlInfo urlInfo) {
        Path path = Paths.get(STORAGE_DIRECTORY, url);
        if (!(path.toFile()).exists()) {
            List<String> data = Arrays.asList(urlInfo.getTime().toString(), urlInfo.getText());
            try {
                Files.write(path, data, UTF_8);
            } catch (IOException exception) {
                LOGGER.error("Failed to index document with url: " + url + " due to exception: "
                        + exception.getMessage() + "\n");
            }
        }
    }
}
