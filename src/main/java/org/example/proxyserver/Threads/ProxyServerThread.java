package org.example.proxyserver.Threads;

import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Config.ProxyConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyServerThread extends Thread {
    private final ServerSocket listenerSocket;
    private final ProxyResources resources;

//    private List<Socket> clients;
    private final AtomicBoolean STOP_SERVER;
    private AtomicBoolean STOP_CLIENT;

    public ProxyServerThread(ServerSocket serverSocket, ProxyResources resources, AtomicBoolean STOP_SERVER) {
        this.listenerSocket = serverSocket;
        this.resources = resources;
        this.STOP_SERVER = STOP_SERVER;
    }

    public void run() {
        STOP_CLIENT = new AtomicBoolean(false);
        while (!STOP_SERVER.get()) {
            try {
                handleAccept();
            } catch (IOException e) {
                // OBSLUGA INNYCH WYJATKOW NIZ TIME OUT
                System.out.println(e.getMessage());
            }
        }
        close();
    }

    private void handleAccept() throws IOException {
        System.out.println("## [" + listenerSocket.getInetAddress() + "] Listening..");
        Socket clientSocket = listenerSocket.accept();

        // SPRAWDZ CZY NALEZY DO ALLOWED IP ADDRESSES

        System.out.println("## [" + listenerSocket.getInetAddress() + "] Client accepted: " + clientSocket.getInetAddress());
        clientSocket.setSoTimeout(ProxyConfig.getConfig().getTimeOut());
//        clients.add(clientSocket);

        ClientHandler clientHandler = new ClientHandler(clientSocket, resources, STOP_CLIENT);
        clientHandler.start();
    }

    private void close() {
        STOP_CLIENT.set(true);
        try {
            listenerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
