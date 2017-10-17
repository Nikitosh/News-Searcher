package com.nikitosh.spbau.frontier;

import com.nikitosh.spbau.parser.*;
import org.apache.logging.log4j.*;

import java.net.*;
import java.util.*;

public class CyclicQueueFrontier implements Frontier {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, DomainUrlsSet> domainUrlsSets = new HashMap<>();
    private Queue<String> domainsQueue = new LinkedList<>();

    @Override
    public DomainUrlsSet getNextDomainUrlsSet() {
        while (!domainsQueue.isEmpty()) {
            String domain = domainsQueue.remove();
            if (!domainUrlsSets.get(domain).isEmpty()) {
                domainsQueue.add(domain);
                return domainUrlsSets.get(domain);
            }
            domainsQueue.add(domain);
        }
        return null;
    }

    @Override
    public void addUrl(String url) {
        try {
            String domain = Parser.getDomainName(url);
            if (!domainUrlsSets.containsKey(domain)) {
                domainUrlsSets.put(domain, new InDegreeDomainUrlsSet());
                domainsQueue.add(domain);
            }
            domainUrlsSets.get(domain).addUrl(url);
        } catch (URISyntaxException exception) {
            LOGGER.error("Failed to parse url: " + url + " due to exception: "
                    + exception.getMessage() + "\n");
        }
    }

    @Override
    public boolean isFinished() {
        for (String domain : domainsQueue) {
            if (!domainUrlsSets.get(domain).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
