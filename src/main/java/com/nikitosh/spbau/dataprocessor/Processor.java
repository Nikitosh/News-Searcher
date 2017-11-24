package com.nikitosh.spbau.dataprocessor;

import java.io.File;
import java.util.List;

public interface Processor {
    List<String> getTermsFromFile(File file);
    List<String> getTermsFromString(String text);
}
