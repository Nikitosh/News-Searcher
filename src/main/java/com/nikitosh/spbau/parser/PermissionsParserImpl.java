package com.nikitosh.spbau.parser;

public class PermissionsParserImpl implements PermissionsParser {
    @Override
    public boolean isCrawlingAllowed(String url) {
        return false;
    }
}
