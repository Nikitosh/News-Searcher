package com.nikitosh.spbau.webcrawler;

import com.nikitosh.spbau.parser.ParserHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.*;

public class WebCrawlerImpl implements WebCrawler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int THREAD_NUMBER = 10;
    private static final int MIN_URLS_SET_SIZE = 0;

    private int currentThreadNumber = 0;
    private List<CrawlerThread> threads = new ArrayList<>();
    private final Set<String> otherUrls = new HashSet<>();
    private Map<String, CrawlerThread> domainThread = new HashMap<>();

    @Override
    public void crawl(List<String> seedUrls) {
        for (int i = 0; i < THREAD_NUMBER; i++) {
            threads.add(new CrawlerThread(otherUrls));
        }
        for (String seedUrl : seedUrls) {
            threads.get(currentThreadNumber).addUrl(seedUrl);
            try {
                domainThread.put(ParserHelper.getDomainName(seedUrl), threads.get(currentThreadNumber));
            } catch (URISyntaxException exception) {
                LOGGER.error("Failed to get domain name from url: " + seedUrl + " due to exception: "
                        + exception.getMessage() + "\n");
            }
            increaseCurrentThreadNumber();
        }
        for (int i = 0; i < THREAD_NUMBER; i++) {
            threads.get(i).start();
        }
        while (true) {
            synchronized (otherUrls) {
                if (otherUrls.size() > MIN_URLS_SET_SIZE) {
                    for (String url : otherUrls) {
                        try {
                            String domain = ParserHelper.getDomainName(url);
                            if (!domainThread.containsKey(domain)) {
                                int oldCurrentThreadNumber = currentThreadNumber;
                                boolean found = true;
                                while (!threads.get(currentThreadNumber).canAddDomain()) {
                                    increaseCurrentThreadNumber();
                                    if (currentThreadNumber == oldCurrentThreadNumber) {
                                        found = false;
                                        break;
                                    }
                                }
                                if (!found) {
                                    break;
                                }
                                System.out.println(currentThreadNumber);
                                domainThread.put(domain, threads.get(currentThreadNumber));
                                increaseCurrentThreadNumber();
                            }
                            domainThread.get(domain).addUrl(url);
                        } catch (URISyntaxException exception) {
                            LOGGER.error("Failed to get domain name from url: " + url + " due to exception: "
                                    + exception.getMessage() + "\n");
                        }
                    }
                    otherUrls.clear();
                } else {
                    Thread.yield();
                }
            }
        }
    }

    private void increaseCurrentThreadNumber() {
        currentThreadNumber++;
        if (currentThreadNumber == THREAD_NUMBER) {
            currentThreadNumber = 0;
        }
    }
}
