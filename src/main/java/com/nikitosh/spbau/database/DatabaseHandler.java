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

public class DatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String DATABASE_PATH = "jdbc:sqlite:../data/page_attributes.db";

    private Connection connection;

    public DatabaseHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            connection = DriverManager.getConnection(DATABASE_PATH);
        } catch (SQLException exception) {
            LOGGER.error("Failed to create database connection due to exception: " + exception.getMessage() + "\n");
        }
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE PageAttributes (" +
                         "id               INT NOT NULL UNIQUE," +
                         "url              TEXT NOT NULL UNIQUE," +
                         "words_count      INT NOT NULL," +
                         "characters_count INT NOT NULL);";
            statement.executeUpdate(sql);
        }  catch (SQLException exception) {
            LOGGER.error("Failed to create database table due to exception: " + exception.getMessage() + "\n");
        }
    }

    public List<PageAttributes> getAllPageAttributes() {
        try (Statement statement = connection.createStatement()) {
            List<PageAttributes> pageAttributes = new ArrayList<PageAttributes>();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT url, words_count, characters_count FROM PageAttributes");
            while (resultSet.next()) {
                pageAttributes.add(new PageAttributes(
                        resultSet.getInt("id"),
                        resultSet.getString("url"),
                        resultSet.getInt("words_count"),
                        resultSet.getInt("characters_count")));
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
                "INSERT INTO PageAttributes(`id`, `url`, `words_count`, `characters_count`) " +
                        "VALUES(?, ?, ?)")) {
            statement.setObject(1, pageAttributes.getId());
            statement.setObject(2, pageAttributes.getUrl());
            statement.setObject(3, pageAttributes.getWordCount());
            statement.setObject(4, pageAttributes.getCharactersCount());
            statement.execute();
        } catch (SQLException exception) {
            LOGGER.error("Failed to insert in database page attributes from url: " + pageAttributes.getUrl()
                    + " due to exception: " + exception.getMessage() + "\n");
        }
    }
}