package com.nikitosh.spbau.webcrawler;

import com.nikitosh.spbau.frontier.CyclicQueueFrontier;
import com.nikitosh.spbau.frontier.DomainUrlsSet;
import com.nikitosh.spbau.frontier.Frontier;
import com.nikitosh.spbau.parser.Parser;
import com.nikitosh.spbau.parser.ParserHelper;
import com.nikitosh.spbau.parser.ParserImpl;
import com.nikitosh.spbau.parser.Permissions;
import com.nikitosh.spbau.parser.PermissionsParser;
import com.nikitosh.spbau.parser.PermissionsParserImpl;
import com.nikitosh.spbau.storage.Storage;
import com.nikitosh.spbau.storage.StorageImpl;
import com.nikitosh.spbau.storage.UrlInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawlerImpl implements WebCrawler {
    private Parser parser = new ParserImpl();
    private PermissionsParser permissionsParser = new PermissionsParserImpl();
    private Frontier frontier = new CyclicQueueFrontier(permissionsParser);
    private Storage storage = new StorageImpl();
    private Set<String> visitedUrls = new HashSet<>();


    @Override
    public void crawl(List<String> seedUrls) {
        for (String url : seedUrls) {
            frontier.addUrl(url);
        }
        while (!frontier.isFinished()) {
            DomainUrlsSet domainUrlsSet = frontier.getNextDomainUrlsSet();
            String url = domainUrlsSet.popNextUrl();
            Permissions permissions = permissionsParser.getPermissions(url);
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
                                frontier.addUrl(link);
                            }
                        }
                    }
                } catch (Exception exception) { }
            }
        }
    }
}
