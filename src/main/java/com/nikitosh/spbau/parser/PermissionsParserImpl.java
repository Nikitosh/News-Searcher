package com.nikitosh.spbau.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.TeeContentHandler;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsParserImpl implements PermissionsParser {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String USER_AGENT = "User-Agent";
    private static final String ROBOTS = "robots";
    private static final String NOFOLLOW = "nofollow";
    private static final String NOINDEX = "noindex";
    private static final String NONE = "none";

    private Map<String, RobotsTxtPermissions> domainPermissions = new HashMap<>();

    @Override
    public Permissions getPermissions(String url) {
        addRobotsTxtPermissions(url);

        if (!isAllowedLink(url)) {
            return new Permissions(false, false);
        }

        boolean follow = true;
        boolean index = true;
        try {
            URL newUrl = new URL(url);
            URLConnection connection = newUrl.openConnection();
            connection.setRequestProperty(USER_AGENT, ParserHelper.CRAWLER_BOT);
            connection.setConnectTimeout(ParserHelper.TIMEOUT);
            connection.connect();
            InputStream input = connection.getInputStream();
            Metadata metadata = new Metadata();
            HtmlParser parser = new HtmlParser();
            TeeContentHandler teeHandler = new TeeContentHandler();
            parser.parse(input, teeHandler, metadata);
            String[] metadataNames = metadata.names();
            ArrayList<String> allRules = new ArrayList<>();
            for (String name : metadataNames) {
                if (name.equals(ROBOTS)) {
                    String content = metadata.get(name);
                    allRules.addAll(Arrays.asList(content.split(",")));
                }
            }

            if (allRules.contains(NOFOLLOW) || allRules.contains(NONE)) {
                follow = false;
            }
            if (allRules.contains(NOINDEX) || allRules.contains(NONE)) {
                index = false;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get meta data from url: " + url + " " + e.getMessage() + "\n");
        }
        return new Permissions(index, follow);
    }

    private void addRobotsTxtPermissions(String url) {
        try {
            String domainUrl = ParserHelper.getDomainName(url);
            if (!domainPermissions.containsKey(domainUrl)) {
                domainPermissions.put(domainUrl, RobotsTxtPermissions.from(domainUrl));
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to get domain name from url: " + url + " due to exception: " + e.getMessage()
                    + "\n");
        }
    }

    private boolean isAllowedLink(String url) {
        try {
            RobotsTxtPermissions permissions = domainPermissions.get(ParserHelper.getDomainName(url));
            List<String> allowedMasks = permissions.getAllowedUrlMasks();
            List<String> disallowedMasks = permissions.getDisallowedUrlMasks();
            int disallowMatchLength = 0;
            int allowMatchLength = 1;
            for (String mask : disallowedMasks) {
                if (ruleMatches(url, mask)) {
                    disallowMatchLength = Math.max(disallowMatchLength, mask.length());
                }
            }
            for (String mask : allowedMasks) {
                if (ruleMatches(url, mask)) {
                    allowMatchLength = Math.max(allowMatchLength, mask.length());
                }
            }
            return allowMatchLength > disallowMatchLength;
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to get domain name from url: " + url + " due to exception: " + e.getMessage()
                    + "\n");
        }
        return false;
    }

    private boolean ruleMatches(String text, String pattern) {
        int patternPos = 0;
        int textPos = 0;

        int patternEnd = pattern.length();
        int textEnd = text.length();

        boolean containsEndChar = pattern.endsWith("$");
        if (containsEndChar) {
            patternEnd -= 1;
        }
        while ((patternPos < patternEnd) && (textPos < textEnd)) {
            int wildcardPos = pattern.indexOf('*', patternPos);
            if (wildcardPos == -1) {
                wildcardPos = patternEnd;
            }
            if (wildcardPos == patternPos) {
                patternPos += 1;
                if (patternPos >= patternEnd) {
                    return true;
                }
                int patternPieceEnd = pattern.indexOf('*', patternPos);
                if (patternPieceEnd == -1) {
                    patternPieceEnd = patternEnd;
                }

                boolean matched = false;
                int patternPieceLen = patternPieceEnd - patternPos;
                while ((textPos + patternPieceLen <= textEnd) && !matched) {
                    matched = true;
                    for (int i = 0; i < patternPieceLen && matched; i++) {
                        if (text.charAt(textPos + i) != pattern.charAt(patternPos + i)) {
                            matched = false;
                        }
                    }
                    if (!matched) {
                        textPos += 1;
                    }
                }
                if (!matched) {
                    return false;
                }
            } else {
                while ((patternPos < wildcardPos) && (textPos < textEnd)) {
                    if (text.charAt(textPos++) != pattern.charAt(patternPos++)) {
                        return false;
                    }
                }
            }
        }
        while ((patternPos < patternEnd) && (pattern.charAt(patternPos) == '*')) {
            patternPos += 1;
        }
        return (patternPos == patternEnd) && ((textPos == textEnd) || !containsEndChar);
    }

}
