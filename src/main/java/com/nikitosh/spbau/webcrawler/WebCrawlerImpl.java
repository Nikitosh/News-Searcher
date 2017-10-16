package com.nikitosh.spbau.webcrawler;

import com.nikitosh.spbau.frontier.*;
import com.nikitosh.spbau.parser.*;
import com.nikitosh.spbau.storage.*;

import java.util.*;

public class WebCrawlerImpl implements WebCrawler {
    private Frontier frontier = new InDegreeFrontier();
    private Parser parser = new ParserImpl();
    private PermissionsParser permissionsParser = new PermissionsParserImpl();
    private Storage storage = new StorageImpl();

    @Override
    public void crawl() {
        while (!frontier.isFinished()) {
            DomainUrlsSet domainUrlsSet = frontier.getNextDomainUrlsSet();
            String url = domainUrlsSet.popNextUrl();
            if (permissionsParser.isCrawlingAllowed(url)) {
                UrlInfo urlInfo = parser.parse(url);
                storage.addDocument(url, urlInfo);
                List<String> links = urlInfo.getLinks();
                for (String link : links) {
                    frontier.addUrl(link);
                }
            }
        }
    }
}
