package org.example.proxyserver.Proxy.Monitoring_utils.Types;

import org.example.proxyserver.Interfaces.TypeHandler;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Topic;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Monitoring_utils.Feedback;

import java.util.Set;

public class Message implements TypeHandler {

    private final ProxyResources resources;

    public Message(ProxyResources resources) {
        this.resources = resources;
    }

    @Override
    public MessageToSend handleSubscriberMode(MessageReceived messageReceived) {
        return Feedback.getReject(messageReceived, "Subscriber cannot send message");
    }

    @Override
    public MessageToSend handleProducerMode(MessageReceived messageReceived) {
        Topic t = getTopic(messageReceived);
        if (t == null) {
            return Feedback.getReject(messageReceived, "Topic does not exist");
        }
        if (t.getProducent() != messageReceived.getClient()) {
            return Feedback.getReject(messageReceived, "You are not topic owner");
        }
        if (t.getSubscribers().isEmpty()) {
            return Feedback.getReject(messageReceived, "No subscribers in this topic");
        }


        Set<Client> clients = t.getSubscribers();
        String data = messageReceived.getJsonData();

        ProxyResources.getResources().addMessageToSend(new MessageToSend(clients, data));
        return Feedback.getAcknowledge(messageReceived, "Message sent to " + clients.size() + " subscribers");
    }

    private Topic getTopic(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();

        String title = mto.getTopic();
        return resources.findTopicByTitle(title);
    }
}
