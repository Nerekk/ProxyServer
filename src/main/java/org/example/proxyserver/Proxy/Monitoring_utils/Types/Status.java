package org.example.proxyserver.Proxy.Monitoring_utils.Types;

import org.example.proxyserver.Interfaces.TypeHandler;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Topic;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Com_resources.Transfer.Payload;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Status implements TypeHandler {

    private final ProxyResources resources;

    public Status(ProxyResources resources) {
        this.resources = resources;
    }

    @Override
    public MessageToSend handleSubscriberMode(MessageReceived messageReceived) {
        return getStatus(messageReceived);
    }

    @Override
    public MessageToSend handleProducerMode(MessageReceived messageReceived) {
        return getStatus(messageReceived);
    }

    private MessageToSend getStatus(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();

        Topic logs = resources.getLogs();

        mto.setId(logs.getUserId());
        mto.setTopic(logs.getTitle());
        mto.setTimestamp(LocalDateTime.now());

        Payload p = mto.getPayload();
        p.setTimestampOfMessage(LocalDateTime.now());
        p.setTopicOfMessage("Status");
        p.setSuccess(true);
        p.setMessage(getTopics());

        String data = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(messageReceived.getClient());

        return new MessageToSend(clients, data);
    }

    private String getTopics() {
        StringBuilder topics = new StringBuilder();

        for (Topic t : resources.getTopics()) {
            String topic = t.getTitle() + " - " + t.getUserId() + "\n";
            topics.append(topic);
        }

        return topics.toString();
    }
}
