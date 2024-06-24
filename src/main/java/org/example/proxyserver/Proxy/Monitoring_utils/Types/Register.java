package org.example.proxyserver.Proxy.Monitoring_utils.Types;

import org.example.proxyserver.Enums.MessageMode;
import org.example.proxyserver.Enums.MessageType;
import org.example.proxyserver.Interfaces.TypeHandler;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Topic;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Com_resources.Transfer.Payload;
import org.example.proxyserver.Proxy.Monitoring_utils.Feedback;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

import java.util.HashSet;
import java.util.Set;

public class Register implements TypeHandler {

    private final ProxyResources resources;

    public Register(ProxyResources resources) {
        this.resources = resources;
    }

    @Override
    public MessageToSend handleSubscriberMode(MessageReceived messageReceived) {
        Topic t = getTopic(messageReceived);
        if (t == null) {
            return Feedback.getReject(messageReceived, "Topic with this name does not exist");
        }

        Client client = messageReceived.getClient();
        t.addSubscriber(client);

        return Feedback.getAcknowledge(messageReceived, "Topic subscribed");
    }

    @Override
    public MessageToSend handleProducerMode(MessageReceived messageReceived) {
        Topic t = getTopic(messageReceived);
        if (t != null) {
            return Feedback.getReject(messageReceived, "Topic with this name already exists");
        }
        addNewTopic(messageReceived);

        return Feedback.getAcknowledge(messageReceived, "Topic added");
    }

    private Topic getTopic(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();

        String title = mto.getTopic();
        return resources.findTopicByTitle(title);
    }

    private void addNewTopic(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();

        String title = mto.getTopic();
        String userId = mto.getId();
        Client client = messageReceived.getClient();

        Topic newTopic = new Topic(title, userId, client);
        resources.addTopic(newTopic);
    }
}
