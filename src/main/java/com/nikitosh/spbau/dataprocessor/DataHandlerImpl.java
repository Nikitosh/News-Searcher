package com.nikitosh.spbau.dataprocessor;

import com.nikitosh.spbau.database.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandlerImpl implements DataHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String INDEX_PATH = "../data/data.index";
    public static final String DICTIONARY_PATH = "../data/terms.dict";

    private Processor processor = new ProcessorImpl();
    private Map<String, Integer> totalEntriesSize = new HashMap<>();
    private Map<String, Integer> totalEntriesCount = new HashMap<>();
    private Map<String, Integer> documentIds = new HashMap<>();
    private Map<String, Long> fileIndents = new HashMap<>();

    @Override
    public void process(String storageDirectoryPath) throws IOException {
        DatabaseHandler.getInstance().createPageAttributes();
        File directory = new File(storageDirectoryPath);
        File[] files = directory.listFiles();
        int id = 0;
        for (File documentFile : files) {
            if (documentFile.isFile()) {
                id++;
                documentIds.put(documentFile.getName(), id);
                List<String> terms = processor.getTermsFromFile(documentFile);
                Map<String, DictionaryEntry> documentInvertedIndex = IndexerHelper.getInvertedIndex(id, terms);
                for (Map.Entry<String, DictionaryEntry> entry : documentInvertedIndex.entrySet()) {
                    String term = entry.getKey();
                    totalEntriesSize.put(term, totalEntriesSize.getOrDefault(term, 0)
                            + entry.getValue().getBytesSize());
                    totalEntriesCount.put(term, totalEntriesCount.getOrDefault(term, 0) + 1);
                }
            }
        }

        long currentIndent = 0;
        RandomAccessFile indexFile = new RandomAccessFile(INDEX_PATH, "rw");
        File dictionaryFile = new File(DICTIONARY_PATH);
        PrintWriter dictionaryWriter = new PrintWriter(dictionaryFile);
        dictionaryWriter.println(totalEntriesSize.size());
        for (String term : totalEntriesSize.keySet()) {
            dictionaryWriter.println(term);
            dictionaryWriter.println(currentIndent);
            dictionaryWriter.println(Math.log(id * 1. / totalEntriesCount.get(term)));
            indexFile.seek(currentIndent);
            indexFile.writeInt(totalEntriesCount.get(term));
            currentIndent += 4;
            fileIndents.put(term, currentIndent);
            currentIndent += totalEntriesSize.get(term);
        }
        dictionaryWriter.close();

        for (File documentFile : files) {
            if (documentFile.isFile()) {
                id = documentIds.get(documentFile.getName());
                List<String> terms = processor.getTermsFromFile(documentFile);
                Map<String, DictionaryEntry> documentInvertedIndex = IndexerHelper.getInvertedIndex(id, terms);
                double vectorLength = 0;
                for (Map.Entry<String, DictionaryEntry> entry : documentInvertedIndex.entrySet()) {
                    String term = entry.getKey();
                    vectorLength += Math.pow(entry.getValue().getOccurrencesPositionsSize()
                            * Math.log(id * 1. / totalEntriesCount.get(term)), 2);
                    long indent = fileIndents.get(term);
                    indexFile.seek(indent);
                    entry.getValue().write(indexFile);
                    fileIndents.put(term, indent + entry.getValue().getBytesSize());
                }
                String url = DatabaseHandler.getInstance().getUrlForFileName(documentFile.getName());
                DatabaseHandler.getInstance().addPageAttributes(
                        PageAttributesExtractor.getPageAttributes(
                                id,
                                documentFile.getName(),
                                terms,
                                TimeExtractor.getTime(url, documentFile),
                                TitleExtractor.getTitle(url, documentFile),
                                Math.sqrt(vectorLength))
                );
            }
        }
        indexFile.close();
    }
}
