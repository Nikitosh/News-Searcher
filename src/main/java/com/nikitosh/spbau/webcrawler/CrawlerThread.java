package com.nikitosh.spbau.webcrawler;

import com.nikitosh.spbau.frontier.CyclicQueueFrontier;
import com.nikitosh.spbau.frontier.DomainUrlsSet;
import com.nikitosh.spbau.frontier.Frontier;
import com.nikitosh.spbau.parser.*;
import com.nikitosh.spbau.storage.Storage;
import com.nikitosh.spbau.storage.StorageImpl;
import com.nikitosh.spbau.storage.UrlInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawlerThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int MAX_SIZE = 100;

    private Parser parser = new ParserImpl();
    private PermissionsParser permissionsParser = new PermissionsParserImpl();
    private Frontier frontier = new CyclicQueueFrontier(permissionsParser);
    private Storage storage = new StorageImpl();
    private Set<String> visitedUrls = new HashSet<>();
    private Set<String> threadDomains = new HashSet<>();
    private final Set<String> otherUrls;

    public CrawlerThread(Set<String> otherUrls) {
        this.otherUrls = otherUrls;
    }

    @Override
    public void run() {
        while (true) {
            if (frontier.isFinished()) {
                Thread.yield();
                continue;
            }
            DomainUrlsSet domainUrlsSet = frontier.getNextDomainUrlsSet();
            String url = domainUrlsSet.popNextUrl();
            Permissions permissions = permissionsParser.getPermissions(url);
            System.out.println("Current url: " + url);
            if (permissions.isIndexingAllowed() || permissions.isFollowingAllowed()) {
                visitedUrls.add(url);
                try {
                    UrlInfo urlInfo = parser.parse(url);
                    if (permissions.isIndexingAllowed()) {
                        storage.addDocument(url, urlInfo);
                    }
                    if (permissions.isFollowingAllowed()) {
                        List<String> links = urlInfo.getLinks();
                        for (String link : links) {
                            if (!visitedUrls.contains(link) && ParserHelper.isValidUrl(link)) {
                                try {
                                    String domain = ParserHelper.getDomainName(link);
                                    if (threadDomains.contains(domain)) {
                                        frontier.addUrl(link);
                                    } else {
                                        synchronized (otherUrls) {
                                            otherUrls.add(link);
                                        }
                                    }
                                } catch (URISyntaxException exception) {
                                    LOGGER.error("Failed to get domain name from url: " + url + " due to exception: "
                                            + exception.getMessage() + "\n");
                                }
                            }
                        }
                    }
                } catch (Exception exception) {
                }
            }
        }
    }

    public void addUrl(String url) {
        try {
            String domain = ParserHelper.getDomainName(url);
            if (!threadDomains.contains(domain)) {
                threadDomains.add(domain);
            }
            frontier.addUrl(url);
        } catch (URISyntaxException exception) {
            LOGGER.error("Failed to get domain name from url: " + url + " due to exception: "
                    + exception.getMessage() + "\n");
        }
    }

    public boolean canAddDomain() {
        return threadDomains.size() < MAX_SIZE;
    }
}
