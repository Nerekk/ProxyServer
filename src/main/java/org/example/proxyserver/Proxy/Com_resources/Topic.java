package org.example.proxyserver.Proxy.Com_resources;

import lombok.Getter;
import org.example.proxyserver.Proxy.Client;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Topic {
    private String title;
    private String userId;
    private final Client producent;
    private final Set<Client> subscribers;

    public Topic(String title, String userId, Client producent) {
        this.title = title;
        this.producent = producent;
        this.userId = userId;
        subscribers = new HashSet<>();
    }

    public void addSubscriber(Client client) {
        subscribers.add(client);
    }

    public void removeSubscriber(Client client) {
        subscribers.remove(client);
    }
}
