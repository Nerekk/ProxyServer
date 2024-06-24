package org.example.proxyserver.Proxy.Monitoring_utils;

import com.google.gson.JsonParseException;
import org.example.proxyserver.Enums.MessageMode;
import org.example.proxyserver.Exceptions.MessageException;
import org.example.proxyserver.Interfaces.TypeHandler;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Monitoring_utils.Types.Message;
import org.example.proxyserver.Proxy.Monitoring_utils.Types.Register;
import org.example.proxyserver.Proxy.Monitoring_utils.Types.Status;
import org.example.proxyserver.Proxy.Monitoring_utils.Types.Withdraw;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

public class MessageManager {
    private final ProxyResources resources;
    private final MTOJsonParser parser;

    private final TypeHandler register, withdraw, message, status;

    public MessageManager(ProxyResources resources) {
        this.resources = resources;
        this.parser = new MTOJsonParser();

        this.register = new Register(resources);
        this.withdraw = new Withdraw(resources);
        this.message = new Message(resources);
        this.status = new Status(resources);
    }

    public void handleMessageReceived() {
        MessageReceived messageReceived = resources.getReceiveQueue().poll();
        MessageTransferObject mto;
        try {
            String data = messageReceived.getJsonData();
            mto = parseMessage(data);
            messageReceived.setMto(mto);
        } catch (MessageException e) {
            System.out.println(e.getMessage());
            // TODO wygeneruj nowy komunikat
        }


        MessageToSend toSend = manageObject(messageReceived);
        System.out.println(toSend.getData());
        resources.addMessageToSend(toSend);
        // wyslij do kolejki wyslania
    }

    private MessageTransferObject parseMessage(String data) throws MessageException {
        MessageTransferObject mto;

        try {
            mto = parser.parseJsonToMessageTransferObject(data);
        } catch (JsonParseException | IllegalArgumentException e) {
            // blad json
            throw new MessageException(e.getMessage());
        }
        return mto;
    }

    private MessageToSend manageObject(MessageReceived messageReceived) {
        switch (messageReceived.getMto().getType()) {
            case register -> {
                return handleType(register, messageReceived);
            }
            case withdraw -> {
                return handleType(withdraw, messageReceived);
            }
//            case reject -> reject(messageReceived);
//            case acknowledge -> acknowledge(messageReceived);
            case message -> {
                return handleType(message, messageReceived);
            }
//            case file -> file(messageReceived);
//            case config -> config(messageReceived);
            case status -> {
                return handleType(status, messageReceived);
            }

            default -> {
                return null;
            }
        }
    }

    public MessageToSend handleType(TypeHandler type, MessageReceived messageReceived) {
        MessageToSend result;

        MessageTransferObject mto = messageReceived.getMto();
        MessageMode mode = mto.getMode();

        if (mode == MessageMode.producer) {
            result = type.handleProducerMode(messageReceived);
        } else if (mode == MessageMode.subscriber) {
            result = type.handleSubscriberMode(messageReceived);
        } else {
            // TODO nieznany mode
            result = null;
        }
        return result;
    }
}
