package com.nikitosh.spbau.dataprocessor;

import java.io.IOException;

public interface DataHandler {
    void process(String storageDirectory) throws IOException;
}
