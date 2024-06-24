package org.example.proxyserver.Proxy.Com_resources;

import lombok.Data;
import lombok.NonNull;
import org.example.proxyserver.Proxy.Client;

import java.util.Set;

@Data
public class MessageToSend {

    @NonNull
    private Set<Client> subscribers;

    @NonNull
    private String data;
}
