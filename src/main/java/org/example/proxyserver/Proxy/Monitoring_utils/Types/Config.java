package org.example.proxyserver.Proxy.Monitoring_utils.Types;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Config implements TypeHandler {

    private final ProxyResources resources;

    public Config(ProxyResources resources) {
        this.resources = resources;
    }


    @Override
    public MessageToSend handleSubscriberMode(MessageReceived messageReceived) {
        return getConfig(messageReceived);
    }

    @Override
    public MessageToSend handleProducerMode(MessageReceived messageReceived) {
        return getConfig(messageReceived);
    }

    public MessageToSend getConfig(MessageReceived messageReceived) {
        MessageTransferObject mto = messageReceived.getMto();
        Payload p = mto.getPayload();
        Topic logs = resources.getLogs();

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get("config.json")));
            p.setTimestampOfMessage(LocalDateTime.now());
            p.setSuccess(true);
            p.setMessage(content);
        } catch (IOException e) {
            return Feedback.getReject(messageReceived, "File reading problem");
        }
        mto.setId(logs.getUserId());
        mto.setTopic(logs.getTitle());
        mto.setTimestamp(p.getTimestampOfMessage());

        String data = MTOJsonParser.parseToString(mto);
        Set<Client> clients = new HashSet<>();
        clients.add(messageReceived.getClient());

        return new MessageToSend(clients, data);
    }
}
