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

    public static MessageToSend handshake(Client c, MessageType messageType, String feedback) {
        Topic logs = ProxyResources.getResources().getLogs();
        MessageTransferObject mto = new MessageTransferObject(messageType, logs.getUserId(), logs.getTitle(), MessageMode.producer);

        boolean success = messageType == MessageType.acknowledge;
        mto.setPayload(new Payload(mto.getTimestamp(), "Handshake", success, feedback));

        String json = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(c);
        return new MessageToSend(clients, json);
    }

    public static MessageToSend disconnect(Client c) {
        Topic logs = ProxyResources.getResources().getLogs();
        MessageTransferObject mto = new MessageTransferObject(MessageType.status, logs.getUserId(), logs.getTitle(), MessageMode.producer);

        mto.setPayload(new Payload(mto.getTimestamp(), "Disconnect", true, "Disconnect by server"));

        String json = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(c);
        return new MessageToSend(clients, json);
    }

}
