package com.nikitosh.spbau.dataprocessor;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeExtractor {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String CLEAN_REGEX = "[zZа-яА-Я]";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final List<DateFormat> PARSERS = Arrays.asList(
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );
    private static final String VALUES_LIST_PATH = "src/main/resources/values_list.txt";
    private static final Map<String, String> LONG_SUBSTITUTIONS = new HashMap<String, String>() {{
        put("[Я|я]нвар[я|ь]", "Jan");
        put("[Ф|ф]еврал[я|ь]", "Feb");
        put("[М|м]арта?", "Mar");
        put("[А|а]прел[я|ь]", "Apr");
        put("[М|м]а[я|ь]", "May");
        put("[И|и]юн[я|ь]", "Jun");
        put("[И|и]юл[я|ь]", "Jul");
        put("[А|а]вгуста?", "Aug");
        put("[С|с]ентябр[я|ь]", "Sep");
        put("[О|о]ктябр[я|ь]", "Oct");
        put("[Н|н]оябр[я|ь]", "Nov");
        put("[Д|д]екабр[я|ь]", "Dec");
        put("[С|с]егодня", "Today");

    }};
    private static final Map<String, String> SHORT_SUBSTITUTIONS = new HashMap<String, String>() {{
        put("[Я|я]нв", "Jan");
        put("[Ф|ф]ев", "Feb");
        put("[М|м]ар", "Mar");
        put("[А|а]пр", "Apr");
        put("[И|и]юн", "Jun");
        put("[И|и]юл", "Jul");
        put("[А|а]вг", "Aug");
        put("[С|с]ент", "Sep");
        put("[О|о]кт", "Oct");
        put("[Н|н]оя", "Nov");
        put("[Д|д]ек", "Dec");
    }};
    private static final String[] ATTRIBUTES = {"class", "itemprop", "property", "name", "content"};
    private static final String[] CONTENTS = {"content", "datetime"};
    private static final List<String> VALUES;

    static {
        try {
            VALUES = new ArrayList<>(Files.readAllLines(Paths.get(VALUES_LIST_PATH)));
        } catch (Exception exception) {
            LOGGER.error("Failed to create list of values due to exception: " + exception.getMessage() + "\n");
            throw new RuntimeException(exception);
        }
    }

    public static Date parseDate(String string) {
        try {
            return DATE_FORMAT.parse(string);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "null";
        }
        return DATE_FORMAT.format(date);
    }

    public static Date getTime(String url, File file) {
        try {
            Document document = Jsoup.parse(file, "UTF-8", url);
            Parser textParser = new Parser();
            for (String value : VALUES) {
                for (String attribute : ATTRIBUTES) {
                    for (String content : CONTENTS) {
                        try {
                            Elements element = document.getElementsByAttributeValue(attribute, value);
                            String tagContent = element.get(0).attr(content);
                            if (!tagContent.equals("")) {
                                Date tagDate = null;
                                for (DateFormat parser : PARSERS) {
                                    try {
                                        tagDate = parser.parse(tagContent);
                                    } catch (Exception exception) {
                                        LOGGER.info("Wrong format in parse " + exception.getMessage() + "\n");
                                    }
                                }
                                if (tagDate != null) {
                                    return tagDate;
                                }
                            }
                        } catch (Exception exception) {
                            LOGGER.info("No elements for current attribute and value\n");
                        }
                    }
                }
            }

            for (String value : VALUES) {
                for (String attribute : ATTRIBUTES) {
                    try {
                        Elements element = document.getElementsByAttributeValue(attribute, value);
                        String textContent = element.text();
                        if (!textContent.equals("")) {
                            try {
                                for (Map.Entry<String, String> pair : LONG_SUBSTITUTIONS.entrySet()) {
                                    textContent = textContent.replaceAll(pair.getKey(), pair.getValue());
                                }
                                for (Map.Entry<String, String> pair : SHORT_SUBSTITUTIONS.entrySet()) {
                                    textContent = textContent.replaceAll(pair.getKey(), pair.getValue());
                                }
                                textContent = textContent.replaceAll(CLEAN_REGEX, "");
                                List<DateGroup> groups = textParser.parse(textContent);
                                List<Date> dates = groups.get(0).getDates();
                                return dates.get(0);
                            } catch (Exception e) {
                                LOGGER.info("Wrong text format for parse" + e.getMessage() + "\n");
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.info("No elements for current attribute and value");
                    }
                }
            }
            return null;
        } catch (IOException exception) {
            return null;
        }
    }
}
