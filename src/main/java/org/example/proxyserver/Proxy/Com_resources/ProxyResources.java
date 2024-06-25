package org.example.proxyserver.Proxy.Com_resources;

import org.example.proxyserver.Gui.Logger;
import org.example.proxyserver.Interfaces.SendListener;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Config.ProxyConfig;

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

    public static void cleanResources() {
        resources = null;
    }

    private void initializeLogs() {
        logs = new Topic("logs", ProxyConfig.getConfig().getServerId(), null);
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

    public Set<Topic> findSubscribesOfClient(Client c) {
        Set<Topic> subs = new HashSet<>();

        for (Topic t : topics) {
            if (t.getSubscribers().contains(c)) subs.add(t);
        }

        return subs;
    }

    public Set<Topic> findProducesOfClient(Client c) {
        Set<Topic> prods = new HashSet<>();

        for (Topic t : topics) {
            if (t.getTitle().equals("logs")) continue;
            if (t.getProducent().equals(c)) prods.add(t);
        }

        return prods;
    }

    public void removeClientData(Client c) {
        Set<Topic> prods = findProducesOfClient(c);
        prods.forEach(this::removeTopic);

        Set<Topic> subs = findSubscribesOfClient(c);
        subs.forEach(topic -> topic.removeSubscriber(c));
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
