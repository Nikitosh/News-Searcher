package com.nikitosh.spbau.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PORT_NUMBER = 8080;

    private ServerSocket serverSocket;
    private ExecutorService taskExecutor;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            taskExecutor = Executors.newCachedThreadPool();
        } catch (IOException exception) {
            LOGGER.error("Failed to create server socket due to exception: " + exception.getMessage() + "\n");
        }
    }

    public void run() {
        taskExecutor.execute(() -> {
            while (true) {
                synchronized (this) {
                    if (serverSocket == null || serverSocket.isClosed()) {
                        break;
                    }
                }
                try {
                    Socket clientSocket = serverSocket.accept();
                    taskExecutor.execute(new ClientHandler(clientSocket));
                } catch (IOException exception) {
                    LOGGER.warn("Exception during accepting client due to exception: " + exception.getMessage() + "\n");
                }
            }
        });
    }
}
