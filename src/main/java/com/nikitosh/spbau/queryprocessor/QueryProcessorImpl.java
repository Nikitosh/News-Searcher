package com.nikitosh.spbau.queryprocessor;

import com.nikitosh.spbau.database.DatabaseHandler;
import com.nikitosh.spbau.database.PageAttributes;
import com.nikitosh.spbau.dataprocessor.Processor;
import com.nikitosh.spbau.dataprocessor.ProcessorImpl;
import com.nikitosh.spbau.dataprocessor.SnippetExtractor;
import com.nikitosh.spbau.dataprocessor.TimeExtractor;
import com.nikitosh.spbau.parser.ParserHelper;
import com.nikitosh.spbau.storage.StorageImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.nikitosh.spbau.dataprocessor.DataHandlerImpl.DICTIONARY_PATH;
import static com.nikitosh.spbau.dataprocessor.DataHandlerImpl.INDEX_PATH;

public class QueryProcessorImpl implements QueryProcessor {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, Long> termIndents = new HashMap<>();
    private Map<String, Double> termIdfs = new HashMap<>();
    private RandomAccessFile indexFile;
    private Processor processor = new ProcessorImpl();
    private Map<Integer, Double> documentScores = new HashMap<>();

    public QueryProcessorImpl() {
        File dictionaryFile = new File(DICTIONARY_PATH);
        try {
            Scanner dictionaryReader = new Scanner(dictionaryFile);
            dictionaryReader.useLocale(Locale.US);
            int termsNumber = dictionaryReader.nextInt();
            for (int i = 0; i < termsNumber; i++) {
                String term = dictionaryReader.next();
                long indent = dictionaryReader.nextLong();
                termIndents.put(term, indent);
                double idf = dictionaryReader.nextDouble();
                termIdfs.put(term, idf);
            }
            indexFile = new RandomAccessFile(INDEX_PATH, "rw");
        } catch (FileNotFoundException exception) {
        }
    }

    @Override
    public List<JSONObject> getDocumentsForQuery(String query) throws IOException {
        documentScores.clear();
        List<String> terms = processor.getTermsFromString(query);
        Map<String, Long> termOccurrencesNumber = terms.stream().collect(
                Collectors.groupingBy(e -> e, Collectors.counting()));
        for (String term : termOccurrencesNumber.keySet()) {
            if (!termIndents.containsKey(term)) {
                continue;
            }
            indexFile.seek(termIndents.get(term));
            int entriesCount = indexFile.readInt();
            for (int i = 0; i < entriesCount; i++) {
                int documentId = indexFile.readInt();
                int occurrencesNumber = indexFile.readInt();
                for (int j = 0; j < occurrencesNumber; j++) {
                    indexFile.readInt();
                }
                documentScores.put(documentId, documentScores.getOrDefault(documentId, 0.)
                        + occurrencesNumber * termOccurrencesNumber.get(term) * Math.pow(termIdfs.get(term), 2));
            }
        }
        for (Map.Entry<Integer, Double> entry : documentScores.entrySet()) {
            entry.setValue(entry.getValue()
                    / DatabaseHandler.getInstance().getPageAttributesForId(entry.getKey()).getLength());
        }
        List<Map.Entry<Integer, Double>> list = new LinkedList<>(documentScores.entrySet());

        list.sort((Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) ->
                o2.getValue().compareTo(o1.getValue()));
        List<Integer> documentIds = list.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        Date currentMinimumTime = Date.from(Instant.now());
        for (int documentId : documentIds) {
            Date time = DatabaseHandler.getInstance().getPageAttributesForId(documentId).getTime();
            if (time != null && time.compareTo(currentMinimumTime) < 0) {
                currentMinimumTime = time;
            }
        }
        final Date minimumTime = currentMinimumTime;
        return documentIds.stream().map(documentId -> {
            JSONObject json = new JSONObject();
            try {
                PageAttributes pageAttributes = DatabaseHandler.getInstance().getPageAttributesForId(documentId);
                String fileName = pageAttributes.getFileName();
                String url = DatabaseHandler.getInstance().getUrlForId(documentId);
                Document document = Jsoup.parse(new File(StorageImpl.HTML_STORAGE_DIRECTORY_PATH + "/" + fileName), "UTF-8", url);
                String snippet = SnippetExtractor.getSnippet(document, new HashSet<>(terms));
                json.put("title", ParserHelper.cropString(pageAttributes.getTitle(), 60));
                json.put("time", TimeExtractor.formatDate(pageAttributes.getTime()));
                json.put("content", snippet);
                json.put("url", DatabaseHandler.getInstance().getUrlForId(documentId));
                json.put("isHighlighted", minimumTime.equals(pageAttributes.getTime()) ? 1 : 0);
                json.put("links", "ria.ru");
            } catch (IOException exception) {
                LOGGER.error("Failed to get article content from file due to exception: " + exception.getMessage()
                        + "\n");
            }
            return json;
        }).collect(Collectors.toList());
    }

}
