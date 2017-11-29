package com.nikitosh.spbau.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageAttributesDatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DATABASE_PATH = "jdbc:sqlite:../data/page_attributes.db";

    private static PageAttributesDatabaseHandler instance = null;

    private Connection connection;

    public static synchronized PageAttributesDatabaseHandler getInstance() {
        if (instance == null)
            instance = new PageAttributesDatabaseHandler();
        return instance;
    }

    private PageAttributesDatabaseHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            connection = DriverManager.getConnection(DATABASE_PATH);
        } catch (SQLException exception) {
            LOGGER.error("Failed to create database connection due to exception: " + exception.getMessage() + "\n");
        }
    }

    public void createPageAttributes() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE PageAttributes (" +
                    "id               INT NOT NULL UNIQUE," +
                    "file_name        TEXT NOT NULL UNIQUE," +
                    "words_count      INT NOT NULL," +
                    "characters_count INT NOT NULL," +
                    "length           REAL NOT NULL);";
            statement.executeUpdate(sql);
        }  catch (SQLException exception) {
            LOGGER.error("Failed to create database table due to exception: " + exception.getMessage() + "\n");
        }
    }

    public void createDocumentUrls() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE DocumentUrls (" +
                    "url       TEXT NOT NULL UNIQUE," +
                    "file_name TEXT NOT NULL UNIQUE);";
            statement.executeUpdate(sql);
        }  catch (SQLException exception) {
            LOGGER.error("Failed to create database table due to exception: " + exception.getMessage() + "\n");
        }
    }

    public List<PageAttributes> getAllPageAttributes() {
        try (Statement statement = connection.createStatement()) {
            List<PageAttributes> pageAttributes = new ArrayList<PageAttributes>();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id, file_name, words_count, characters_count, length FROM PageAttributes");
            while (resultSet.next()) {
                pageAttributes.add(new PageAttributes(
                        resultSet.getInt("id"),
                        resultSet.getString("file_name"),
                        resultSet.getInt("words_count"),
                        resultSet.getInt("characters_count"),
                        resultSet.getDouble("length")
                ));
            }
            return pageAttributes;

        } catch (SQLException exception) {
            LOGGER.error("Failed to get page attributes from database due to exception: " + exception.getMessage()
                    + "\n");
            return Collections.emptyList();
        }
    }

    public void addPageAttributes(PageAttributes pageAttributes) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO PageAttributes(`id`, `file_name`, `words_count`, `characters_count`, `length`) " +
                        "VALUES(?, ?, ?, ?, ?)")) {
            statement.setObject(1, pageAttributes.getId());
            statement.setObject(2, pageAttributes.getFileName());
            statement.setObject(3, pageAttributes.getWordCount());
            statement.setObject(4, pageAttributes.getCharactersCount());
            statement.setObject(5, pageAttributes.getLength());
            statement.execute();
        } catch (SQLException exception) {
            LOGGER.error("Failed to insert in database page attributes from filename: " + pageAttributes.getFileName()
                    + " due to exception: " + exception.getMessage() + "\n");
        }
    }

    public void addDocumentUrl(String url, String fileName) {
        System.out.println(url);
        System.out.println(fileName);
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO DocumentUrls(`url`, `file_name`) " +
                        "VALUES(?, ?)")) {
            statement.setObject(1, url);
            statement.setObject(2, fileName);
            statement.execute();
        } catch (SQLException exception) {
            LOGGER.error("Failed to insert in database document urls from url: " + url
                    + " due to exception: " + exception.getMessage() + "\n");
        }
    }

    public PageAttributes getPageAttributesForId(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id, file_name, words_count, characters_count, length FROM PageAttributes WHERE id="
                            + id);
            return new PageAttributes(
                    resultSet.getInt("id"),
                    resultSet.getString("file_name"),
                    resultSet.getInt("words_count"),
                    resultSet.getInt("characters_count"),
                    resultSet.getDouble("length")
            );
        } catch (SQLException exception) {
            LOGGER.error("Failed to get page attributes from database due to exception: " + exception.getMessage()
                    + "\n");
            return null;
        }
    }

    public String getUrlForId(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT url " +
                         "FROM (DocumentUrls " +
                         "INNER JOIN PageAttributes ON (DocumentUrls.file_name=PageAttributes.file_name)) " +
                         "WHERE id=" + id);
            return resultSet.getString("url");
        } catch (SQLException exception) {
            exception.printStackTrace();
            LOGGER.error("Failed to get document url from database due to exception: " + exception.getMessage()
                    + "\n");
            return null;
        }
    }
}