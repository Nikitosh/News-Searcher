package com.nikitosh.spbau.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RobotsTxtPermissions {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String ROBOTS_TXT = "/robots.txt";
    private static final String USER_AGENT_TOKEN = "User-agent:";
    private static final String USER_AGENT_MASK = "*";
    private static final String DISALLOW_TOKEN = "Disallow:";
    private static final String ALLOW_TOKEN = "Allow:";
    private static final String CRAWL_DELAY_TOKEN = "Crawl-delay:";

    private List<String> allowedUrlMasks;
    private List<String> disallowedUrlMasks;
    private double delayInSeconds;

    public RobotsTxtPermissions(List<String> allowedUrlMasks, List<String> disallowedUrlMasks, double delayInSeconds) {
        this.allowedUrlMasks = allowedUrlMasks;
        this.disallowedUrlMasks = disallowedUrlMasks;
        this.delayInSeconds = delayInSeconds;
    }

    public List<String> getAllowedUrlMasks() {
        return this.allowedUrlMasks;
    }

    public List<String> getDisallowedUrlMasks() {
        return this.disallowedUrlMasks;
    }

    public double getDelayInSeconds() {
        return this.delayInSeconds;
    }

    public static RobotsTxtPermissions from(String url) {
        ArrayList<String> allow = new ArrayList<>();
        ArrayList<String> disallow = new ArrayList<>();
        double crawlDelay = -1;
        String robotsUrl = url + ROBOTS_TXT;
        try {
            Document document = ParserHelper.getDocument(robotsUrl);
            String content = ParserHelper.getWholeText(document);
            ArrayList<String> tokens = new ArrayList<>(Arrays.asList(content.split(" ")));

            int ind = -1;
            for (int i = 0; i < tokens.size() - 1; i++) {
                if (tokens.get(i).equals(USER_AGENT_TOKEN) && tokens.get(i + 1).equals(USER_AGENT_MASK)) {
                    ind = i + 2;
                    break;
                }
            }

            if (ind != -1) {
                tokens = new ArrayList<>(tokens.subList(ind, tokens.size()));
                int nextInd = tokens.indexOf(USER_AGENT_TOKEN);
                if (nextInd == -1) {
                    nextInd = tokens.size();
                }
                for (int i = 0; i < nextInd; i++) {
                    switch (tokens.get(i)) {
                        case DISALLOW_TOKEN:
                            if (i + 1 < nextInd) {
                                disallow.add(tokens.get(i + 1));
                            }
                            break;
                        case ALLOW_TOKEN:
                            allow.add(tokens.get(i + 1));
                            break;
                        case CRAWL_DELAY_TOKEN:
                            crawlDelay = Double.valueOf(tokens.get(i + 1));
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get data from robots page of url: " + url + " due to exception: "
                    + e.getMessage() + "\n");
        }
        return new RobotsTxtPermissions(allow, disallow, crawlDelay);
    }
}
