package org.example.proxyserver.Proxy.Com_resources;

import org.example.proxyserver.Gui.Logger;
import org.example.proxyserver.Interfaces.SendListener;
import org.example.proxyserver.Proxy.Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyResources {
    private final List<Topic> topics;
    private Topic logs;

    private final Queue<MessageReceived> receiveQueue;

    private final Queue<MessageToSend> sendQueue;
    private final SendListener sendListener;

    private final Set<Client> globalClientList;
    public final static int CLIENT_ADD = 0;
    public final static int CLIENT_REMOVE = 1;

    private static volatile ProxyResources resources;

    private ProxyResources(SendListener sendListener) {
        this.sendListener = sendListener;
        this.topics = new ArrayList<>();
        this.receiveQueue = new LinkedBlockingQueue<>();
        this.sendQueue = new LinkedBlockingQueue<>();
        this.globalClientList = new HashSet<>();

        initializeLogs();
    }

    public static ProxyResources getResources() {
        ProxyResources res = resources;

        if (res != null) {
            return res;
        }
        synchronized (ProxyResources.class) {
            if (resources == null) {
                resources = new ProxyResources(createCallback());
            }
            return resources;
        }
    }

    private void initializeLogs() {
        logs = new Topic("logs", "ServerId", null);
        topics.add(logs);
    }

    public Topic getLogs() {
        return logs;
    }

    public synchronized boolean manageGlobalClientList(int op, Client client) {
        switch (op) {
            case CLIENT_ADD -> {
                if (doesClientWithIdExist(client)) {
                    return false;
                } else {
                    globalClientList.add(client);
                    return true;
                }
            }

            case CLIENT_REMOVE -> {
                if (doesClientWithIdExist(client)) {
                    globalClientList.remove(client);
                    return true;
                } else {
                    return false;
                }
            }
            default -> {
                return false;
            }
        }
    }

    private boolean doesClientWithIdExist(Client client) {
        for (Client c : globalClientList) {
            if (c.getUserId().equals(client.getUserId())) return true;
        }
        return false;
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        Logger.getInstance().log(Logger.INFO, "Topic " + topic.getTitle() + " added!");
    }

    public void removeTopic(Topic topic) {
        topics.remove(topic);
        Logger.getInstance().log(Logger.INFO, "Topic " + topic.getTitle() + " deleted!");
    }

    public Topic findTopicByTitle(String title) {
        for (Topic t : topics) {
            if (t.getTitle().equals(title)) return t;
        }
        return null;
    }

    public synchronized void addMessageReceived(MessageReceived messageReceived) {
        receiveQueue.add(messageReceived);
        Logger.getInstance().log(Logger.INFO, "New message received from " + messageReceived.getClient().getUserId());
    }

    public synchronized void addMessageToSend(MessageToSend messageToSend) {
        sendQueue.add(messageToSend);
        Logger.getInstance().log(Logger.INFO, "New message moved to send!");
        sendListener.callSend();
    }

    public synchronized MessageReceived pollMessageReceived() {
        return receiveQueue.poll();
    }

    public synchronized MessageToSend pollMessageToSend() {
        return sendQueue.poll();
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public Queue<MessageReceived> getReceiveQueue() {
        return receiveQueue;
    }

    public Queue<MessageToSend> getSendQueue() {
        return sendQueue;
    }

    private static SendListener createCallback() {
        return () -> {
            MessageToSend messageToSend = getResources().pollMessageToSend();

            for (Client sub : messageToSend.getSubscribers()) {
                DataOutputStream out = new DataOutputStream(sub.getOutputStream());
                try {
                    out.writeUTF(messageToSend.getData());
                } catch (IOException e) {
                    // TODO obsluz wyjatek
                    throw new RuntimeException(e);
                }
                // wysylaj dane do uzytkownikow
            }
        };
    }
}
