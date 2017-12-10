package com.nikitosh.spbau.queryprocessor;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public interface QueryProcessor {
    List<JSONObject> getDocumentsForQuery(String query) throws IOException;
}
