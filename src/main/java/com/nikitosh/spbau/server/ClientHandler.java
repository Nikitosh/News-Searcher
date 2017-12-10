package com.nikitosh.spbau.server;

import com.nikitosh.spbau.queryprocessor.QueryProcessor;
import com.nikitosh.spbau.queryprocessor.QueryProcessorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    private Socket socket;
    private QueryProcessor queryProcessor = new QueryProcessorImpl();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try (
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                String query;
                while ((query = reader.readLine()) != null) {
                    writer.println(queryProcessor.getDocumentsForQuery(query));
                }
            } catch (IOException exception) {
                LOGGER.error("Failed to handle server request due to exception: " + exception.getMessage() + "\n");
            }
        }
    }
}
