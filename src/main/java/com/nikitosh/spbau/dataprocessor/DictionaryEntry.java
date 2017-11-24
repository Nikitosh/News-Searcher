package com.nikitosh.spbau.dataprocessor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class DictionaryEntry {
    private int documentId;
    private List<Integer> occurrencesPositions;

    public DictionaryEntry(int documentId, List<Integer> occurrencesPositions) {
        this.documentId = documentId;
        this.occurrencesPositions = occurrencesPositions;
    }

    public void write(RandomAccessFile randomAccessFile) throws IOException {
        randomAccessFile.writeInt(documentId);
        randomAccessFile.writeInt(occurrencesPositions.size());
        for (int occurrencePosition : occurrencesPositions) {
            randomAccessFile.writeInt(occurrencePosition);
        }
    }

    public int getBytesSize() {
        return (2 + occurrencesPositions.size()) * 4;
    }

    public int getOccurrencesPositionsSize() {
        return occurrencesPositions.size();
    }
}
