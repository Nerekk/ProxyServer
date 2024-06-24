package org.example.proxyserver.Interfaces;

import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;

public interface TypeHandler {
    MessageToSend handleSubscriberMode(MessageReceived messageReceived);
    MessageToSend handleProducerMode(MessageReceived messageReceived);
}
