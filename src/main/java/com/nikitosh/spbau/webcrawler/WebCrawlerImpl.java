package com.nikitosh.spbau.webcrawler;

import com.nikitosh.spbau.frontier.CyclicQueueFrontier;
import com.nikitosh.spbau.frontier.DomainUrlsSet;
import com.nikitosh.spbau.frontier.Frontier;
import com.nikitosh.spbau.parser.Parser;
import com.nikitosh.spbau.parser.ParserImpl;
import com.nikitosh.spbau.parser.Permissions;
import com.nikitosh.spbau.parser.PermissionsParser;
import com.nikitosh.spbau.parser.PermissionsParserImpl;
import com.nikitosh.spbau.storage.Storage;
import com.nikitosh.spbau.storage.StorageImpl;
import com.nikitosh.spbau.storage.UrlInfo;

import java.util.List;

public class WebCrawlerImpl implements WebCrawler {
    private Frontier frontier = new CyclicQueueFrontier();
    private Parser parser = new ParserImpl();
    private PermissionsParser permissionsParser = new PermissionsParserImpl();
    private Storage storage = new StorageImpl();

    @Override
    public void crawl() {
        while (!frontier.isFinished()) {
            DomainUrlsSet domainUrlsSet = frontier.getNextDomainUrlsSet();
            String url = domainUrlsSet.popNextUrl();
            Permissions permissions = permissionsParser.getPermissions(url);
            if (permissions.isIndexingAllowed() || permissions.isFollowingAllowed()) {
                UrlInfo urlInfo = parser.parse(url);
                if (permissions.isIndexingAllowed()) {
                    storage.addDocument(url, urlInfo);
                }
                if (permissions.isFollowingAllowed()) {
                    List<String> links = urlInfo.getLinks();
                    for (String link : links) {
                        frontier.addUrl(link);
                    }
                }
            }
        }
    }
}
