package org.example.proxyserver.Proxy.Com_resources;

import lombok.Data;
import lombok.NonNull;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;

@Data
public class MessageReceived {

    @NonNull
    private Client client;

    @NonNull
    private String jsonData;

    private MessageTransferObject mto;
}
