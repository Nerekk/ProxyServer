package org.example.proxyserver.Proxy;

import org.example.proxyserver.FXController;
import org.example.proxyserver.Threads.ProxyServerThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ProxyServerManager {
    private FXController c;
    private ProxyConfig config;

    private String serverID;
    private String[] listenAddresses, allowedAddresses;
    private int listenPort, timeOut, sizeLimit;

    private List<ProxyServerThread> servers;

    private volatile boolean STOP_SERVER;


    public ProxyServerManager(FXController c, ProxyConfig config) {
        this.c = c;
        this.config = config;
        extractConfig(config);

        servers = new ArrayList<>();
    }

    private void extractConfig(ProxyConfig config) {
        serverID = config.getServerId();
        listenAddresses = config.getListenAddresses();
        allowedAddresses = config.getAllowedAddresses();
        listenPort = config.getListenedPort();
        timeOut = config.getTimeOut();
        sizeLimit = config.getSizeLimit();
    }

    public void run() throws IOException {
        STOP_SERVER = false;
        if (allAddressesListened()) {
            createGeneralSocket();
        } else {
            createListenedSockets();
        }

        launchSockets();
    }

    public void stop() {
        STOP_SERVER = true;
    }

    private void launchSockets() {
        servers.forEach(Thread::start);

        System.out.println("#Launching sockets..");
    }

    private void createListenedSockets() throws IOException {
        for (String address : listenAddresses) {
            ServerSocket socket = buildSocket(address, listenPort);
            servers.add(new ProxyServerThread(socket, STOP_SERVER));

            System.out.println("#Created socket for " + address);
        }
    }

    private ServerSocket buildSocket(String address, int port) throws IOException {
        ServerSocket socket = new ServerSocket();
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        socket.bind(socketAddress);
        socket.setSoTimeout(timeOut);

        return socket;
    }

    private void createGeneralSocket() throws IOException {
        ServerSocket socket = buildGeneralSocket(listenPort);
        servers.add(new ProxyServerThread(socket, STOP_SERVER));

        System.out.println("#Created socket for GENERAL");
    }

    private ServerSocket buildGeneralSocket(int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout(timeOut);

        return socket;
    }

    private boolean allAddressesListened() {
        return listenAddresses.length == 1 && listenAddresses[0].equals("*");
    }
}
