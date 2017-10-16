package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.*;

public interface Parser {
    UrlInfo parse(String url);
    String getDomain(String url);
}
