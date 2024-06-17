package org.example.proxyserver.Threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ProxyServerThread extends Thread {
    private final ServerSocket listenerSocket;
    private List<Socket> clients;
    private volatile boolean STOP_SERVER;
    private volatile boolean STOP_CLIENT;

    public ProxyServerThread(ServerSocket serverSocket, boolean STOP_SERVER) throws IOException {
        this.listenerSocket = serverSocket;
        this.STOP_SERVER = STOP_SERVER;
    }

    public void run() {
        STOP_CLIENT = false;
        while (!STOP_SERVER) {
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
        clients.add(clientSocket);

        ClientHandler clientHandler = new ClientHandler(clientSocket, STOP_CLIENT);
        clientHandler.start();
    }

    private void close() {
        STOP_CLIENT = true;
        try {
            listenerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
