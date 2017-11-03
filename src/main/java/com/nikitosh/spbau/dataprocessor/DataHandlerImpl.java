package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.database.DatabaseHandler;

import java.io.File;
import java.util.List;

public class DataHandlerImpl implements DataHandler {
    Processor processor = new ProcessorImpl();
    DatabaseHandler databaseHandler = new DatabaseHandler();

    @Override
    public void process(String storageDirectoryPath) {
        File directory = new File(storageDirectoryPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                List<String> terms = processor.getTermsFromFile(file);
                databaseHandler.addPageAttributes(PageAttributesExtractor.getPageAttributes(file.getName(), terms));
            }
        }
    }
}
