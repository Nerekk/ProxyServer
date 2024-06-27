package org.example.proxyserver.Proxy.Monitoring_utils.Types;

import org.example.proxyserver.Interfaces.TypeHandler;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Topic;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Monitoring_utils.Feedback;

public class Withdraw implements TypeHandler {

    private final ProxyResources resources;

    public Withdraw(ProxyResources resources) {
        this.resources = resources;
    }

    @Override
    public MessageToSend handleSubscriberMode(MessageReceived messageReceived) {
        Topic t = getTopic(messageReceived);
        if (t == null) return Feedback.getReject(messageReceived, "Topic with this name does not exist");

        Client client = messageReceived.getClient();
        t.removeSubscriber(client);

        return Feedback.getAcknowledge(messageReceived, "Topic unsubscribed");
    }

    @Override
    public MessageToSend handleProducerMode(MessageReceived messageReceived) {
        Topic t = getTopic(messageReceived);
        if (t == null) return Feedback.getReject(messageReceived, "Topic with this name does not exist");
        if (t.getProducent() != messageReceived.getClient()) return Feedback.getReject(messageReceived, "You are not topic owner");
        resources.removeTopic(t);

        return Feedback.getAcknowledge(messageReceived, "Topic deleted");
    }

    private Topic getTopic(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();

        String title = mto.getTopic();
        return resources.findTopicByTitle(title);
    }
}
