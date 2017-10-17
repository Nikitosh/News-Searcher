package com.nikitosh.spbau.parser;

import org.apache.logging.log4j.*;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.TeeContentHandler;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.nikitosh.spbau.parser.Parser.*;
import static com.nikitosh.spbau.parser.ParserImpl.getDocument;
import static com.nikitosh.spbau.parser.ParserImpl.getWholeText;

public class PermissionsParserImpl implements PermissionsParser {

    private Map<String, RobotsTxtPermissions> domainPermissions = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String USER_AGENT_TOKEN = "User-agent:";
    private static final String USER_AGENT_MASK = "*";
    private static final String DISALLOW_TOKEN = "Disallow:";
    private static final String ALLOW_TOKEN = "Allow:";
    private static final String CRAWL_TOKEN = "Crawl-delay:";
    private static final String NOFOLLOW = "nofollow";
    private static final String NOINDEX = "noindex";
    private static final String NONE = "none";

    @Override
    public Permissions getPermissions(String url) {

        getRobotsTxtPermissions(url);

        boolean follow = true;
        boolean index = true;
        try {
            URL newUrl = new URL(url);
            URLConnection connection = newUrl.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(TIMEOUT);
            connection.connect();
            InputStream input = connection.getInputStream();
            Metadata metadata = new Metadata();
            HtmlParser parser = new HtmlParser();
            TeeContentHandler teeHandler = new TeeContentHandler();
            parser.parse(input, teeHandler, metadata);
            String[] metadataNames = metadata.names();
            ArrayList<String> allRules = new ArrayList<>();
            for (String name : metadataNames) {
                if (name.equals("robots")) {
                    String content = metadata.get(name);
                    allRules.addAll(Arrays.asList(content.split(",")));
                }
            }

            if (allRules.contains(NOFOLLOW)) {
                follow = false;
            }
            if (allRules.contains(NOINDEX)) {
                index = false;
            }

            if (allRules.contains(NONE)) {
                follow = false;
                index = false;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get meta data from url: " + url + " " + e.getMessage() + "\n");
        }
        return new Permissions(index, follow);

    }

    private void getRobotsTxtPermissions(String url) {
        try {
            String domainUrl = getDomainName(url);
            if (!domainPermissions.containsKey(domainUrl)) {
                addRobotsTxtPermissions(domainUrl);
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to get domain name from url: " + url + " " + e.getMessage() + "\n");
        }
    }

    private void addRobotsTxtPermissions(String url) {

        ArrayList<String> allow = new ArrayList<>();
        ArrayList<String> disallow = new ArrayList<>();
        int crawlDelay = -1;
        String robotsUrl = url + "/robots.txt";
        try {
            Document document = getDocument(robotsUrl);
            String content = getWholeText(document);
            ArrayList<String> tokens = new ArrayList<>(Arrays.asList(content.split(" ")));

            int ind = -1;
            for (int i = 0; i < tokens.size() - 1; i++) {
                if (tokens.get(i).equals(USER_AGENT_TOKEN) && tokens.get(i + 1).equals(USER_AGENT_MASK)) {
                    ind = i + 2;
                    break;
                }
            }
            if (ind == -1) {
                domainPermissions.put(url, new RobotsTxtPermissions(allow, disallow, crawlDelay));
                return;
            }

            tokens = new ArrayList<>(tokens.subList(ind, tokens.size()));
            int nextInd = tokens.indexOf(USER_AGENT_TOKEN);
            if (nextInd == -1) {
                nextInd = tokens.size();
            }
            for (int i = 0; i < nextInd; i++) {
                switch (tokens.get(i)) {
                    case DISALLOW_TOKEN:
                        disallow.add(tokens.get(i + 1));
                        break;
                    case ALLOW_TOKEN:
                        allow.add(tokens.get(i + 1));
                        break;
                    case CRAWL_TOKEN:
                        crawlDelay = Integer.valueOf(tokens.get(i + 1));
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get data from robots page of url: " + url + " " + e.getMessage() + "\n");
        }
        domainPermissions.put(url, new RobotsTxtPermissions(allow, disallow, crawlDelay));
    }
}
