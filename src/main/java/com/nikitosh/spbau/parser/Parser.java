package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.UrlInfo;

public interface Parser {
    UrlInfo parse(String url);
}
