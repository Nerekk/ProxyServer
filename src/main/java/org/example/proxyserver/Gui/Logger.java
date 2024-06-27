package org.example.proxyserver.Gui;

import javafx.scene.control.TextArea;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.Topic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logger {
    public static final String INFO = "[INFO]";
    public static final String ERROR = "[ERROR]";

    private final TextArea topics, logger, servers;

    private static Logger instance;

    public Logger(TextArea topicsText, TextArea loggerText, TextArea serversText) {
        this.topics = topicsText;
        this.logger = loggerText;
        this.servers = serversText;
    }

    public static Logger getInstance() {
        return instance;
    }

    public static void setInstance(Logger logger) {
        instance = logger;
    }

    public synchronized void log(String type, String message) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = "[" + currentTime.format(formatter) + "]";
        String currentDebug = logger.getText();

        String info;
        message = type + " " + message;
        if (currentDebug.isEmpty()) {
            info = currentTimeString + message;
        } else {
            info = "\n" + currentTimeString + message;
        }

        logger.appendText(info);
    }

    public void logTopics(List<Topic> topicList) {
        topics.clear();
        for (Topic t : topicList) {
            String topic = "# " + t.getTitle() + " - " + t.getUserId() + "\n";
            if (topics.getText().isEmpty()) {
                topics.appendText(topic);
            } else {
                String newLineTopic = "\n" + topic;
                topics.appendText(newLineTopic);
            }

            for (Client c : t.getSubscribers()) {
                topics.appendText("--- " + c.getUserId() + "\n");
            }
        }
    }

    public void logServers(int port, String[] listenAddresses) {
        servers.clear();
        servers.appendText("# Listening port: " + port + "\n");
        servers.appendText("# Listened addresses: \n");
        for (String address : listenAddresses) {
            servers.appendText("- " + address + "\n");
        }
    }
}
