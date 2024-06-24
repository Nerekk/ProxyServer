package org.example.proxyserver.Proxy;

import org.example.proxyserver.FXController;
import org.example.proxyserver.Gui.Logger;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Config.ProxyConfig;
import org.example.proxyserver.Threads.MonitoringThread;
import org.example.proxyserver.Threads.ProxyServerThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyServerManager {
    private final FXController c;
    private final ProxyConfig config;
    private final ProxyResources resources;

    private String serverID;
    private String[] listenAddresses, allowedAddresses;
    private int listenPort, timeOut, sizeLimit;

    private List<ProxyServerThread> servers;

    private AtomicBoolean STOP_SERVER;

    private MonitoringThread monitoring;

    public ProxyServerManager(FXController c) {
        this.c = c;

        this.config = ProxyConfig.getConfig();
        this.resources = ProxyResources.getResources();

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
        STOP_SERVER = new AtomicBoolean(false);
        if (allAddressesListened()) {
            createGeneralSocket();
        } else {
            createListenedSockets();
        }
        refreshGui();

        launchSockets();
        monitoring = new MonitoringThread(config, resources, STOP_SERVER);
    }

    public void stop() {
        STOP_SERVER.set(true);
    }

    public void refreshGui() {
        c.logger.logServers(listenPort, listenAddresses);
        c.logger.logTopics(resources.getTopics());
        c.logger.log(Logger.INFO, "Refreshed");
    }

    private void launchSockets() {
        servers.forEach(Thread::start);

//        System.out.println("#Launching sockets..");
        c.logger.log(Logger.INFO, "Launching sockets..");
    }

    private void createListenedSockets() throws IOException {
        for (String address : listenAddresses) {
            ServerSocket socket = buildSocket(address, listenPort);
            servers.add(new ProxyServerThread(socket, resources, STOP_SERVER));

//            System.out.println("#Created socket for " + address);
            c.logger.log(Logger.INFO, "Created socket for " + address);
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
        servers.add(new ProxyServerThread(socket, resources, STOP_SERVER));

//        System.out.println("#Created socket for GENERAL");
        c.logger.log(Logger.INFO, "Created socket for general address");
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
