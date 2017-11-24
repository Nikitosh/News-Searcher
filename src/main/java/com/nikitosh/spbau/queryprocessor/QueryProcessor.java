package com.nikitosh.spbau.queryprocessor;

import java.io.IOException;
import java.util.List;

public interface QueryProcessor {
    List<Integer> getDocumentsForQuery(String query) throws IOException;
}
