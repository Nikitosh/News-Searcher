package com.nikitosh.spbau.parser;

import com.nikitosh.spbau.storage.*;

import java.net.*;

public interface Parser {
    static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    UrlInfo parse(String url);
    String getDomain(String url);
}
