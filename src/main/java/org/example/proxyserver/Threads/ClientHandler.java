package org.example.proxyserver.Threads;

import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler extends Thread {
    private Client client;
    private DataInputStream in;
    private final ProxyResources resources;

    private final AtomicBoolean STOP_CLIENT;

    public ClientHandler(Socket clientSocket, ProxyResources resources, AtomicBoolean STOP_CLIENT) throws IOException {
        this.client = new Client(clientSocket);
        this.resources = resources;
        this.STOP_CLIENT = STOP_CLIENT;

        this.in = new DataInputStream(this.client.getInputStream());
    }

    public void run() {
        System.out.println("### [" + client.getClientSocket().getInetAddress() + "] Handling client..");

        while (!STOP_CLIENT.get()) {
            String jsonData;
            try {
                jsonData = in.readUTF();
                System.out.println(jsonData);
            } catch (IOException _) {
                continue;
            }

            if (!jsonData.isEmpty())
                resources.addMessageReceived(new MessageReceived(client, jsonData));
        }
        System.out.println("### [" + client.getClientSocket().getInetAddress() + "] Handling ended");
    }
}
