package org.example.proxyserver.Proxy.Monitoring_utils;

import org.example.proxyserver.Enums.MessageMode;
import org.example.proxyserver.Enums.MessageType;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Topic;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Com_resources.Transfer.Payload;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

import java.util.HashSet;
import java.util.Set;

public class Feedback {

    public static MessageToSend getAcknowledge(MessageReceived messageReceived, String feedback) {
        Topic logs = ProxyResources.getResources().getLogs();
        MessageTransferObject mto = new MessageTransferObject(MessageType.acknowledge, logs.getUserId(), logs.getTitle(), MessageMode.producer);

        MessageTransferObject mr = MTOJsonParser.parseJsonToMessageTransferObject(messageReceived.getJsonData());
        mto.setPayload(new Payload(mr.getTimestamp(), mr.getTopic(), true, feedback));

        String json = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(messageReceived.getClient());
        return new MessageToSend(clients, json);
    }

    public static MessageToSend getReject(MessageReceived messageReceived, String feedback) {
        Topic logs = ProxyResources.getResources().getLogs();
        MessageTransferObject mto = new MessageTransferObject(MessageType.reject, logs.getUserId(), logs.getTitle(), MessageMode.producer);

        MessageTransferObject mr = MTOJsonParser.parseJsonToMessageTransferObject(messageReceived.getJsonData());
        mto.setPayload(new Payload(mr.getTimestamp(), mr.getTopic(), false, feedback));

        String json = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(messageReceived.getClient());
        return new MessageToSend(clients, json);
    }

}
