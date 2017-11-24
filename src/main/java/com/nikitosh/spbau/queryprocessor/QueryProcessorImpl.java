package com.nikitosh.spbau.queryprocessor;

import com.nikitosh.spbau.database.DatabaseHandler;
import com.nikitosh.spbau.dataprocessor.Processor;
import com.nikitosh.spbau.dataprocessor.ProcessorImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.nikitosh.spbau.dataprocessor.DataHandlerImpl.DICTIONARY_PATH;
import static com.nikitosh.spbau.dataprocessor.DataHandlerImpl.INDEX_PATH;

public class QueryProcessorImpl implements QueryProcessor {

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
    public List<Integer> getDocumentsForQuery(String query) throws IOException {
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
        return list.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
