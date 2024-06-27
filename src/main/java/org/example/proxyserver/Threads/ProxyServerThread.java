package org.example.proxyserver.Threads;

import org.apache.commons.net.util.SubnetUtils;
import org.example.proxyserver.Enums.MessageType;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Config.ProxyConfig;
import org.example.proxyserver.Proxy.Monitoring_utils.Feedback;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyServerThread extends Thread {
    private final ServerSocket listenerSocket;
    private final ProxyResources resources;

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
            } catch (IOException _) {}
        }
        close();
    }

    private void handleAccept() throws IOException {
        Socket clientSocket = listenerSocket.accept();

        Client c = new Client(clientSocket);
        //handshake
        if (!handshake(c)) {
            c.getInputStream().close();
            c.getOutputStream().close();
            c.getClientSocket().close();

            System.out.println("## [" + listenerSocket.getInetAddress() + "] Client not allowed: " + clientSocket.getInetAddress());

            return;
        }

        System.out.println("## [" + listenerSocket.getInetAddress() + "] Client accepted: " + clientSocket.getInetAddress());
        clientSocket.setSoTimeout(ProxyConfig.getConfig().getTimeOut());

        ClientHandler clientHandler = new ClientHandler(c, resources, STOP_CLIENT);
        clientHandler.start();
    }

    private boolean handshake(Client client) throws IOException {
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        DataInputStream in = new DataInputStream(client.getInputStream());

        String json = in.readUTF();
        MessageTransferObject mto = MTOJsonParser.parseJsonToMessageTransferObject(json);
        MessageToSend mts;

        boolean isAllowed = isIpAllowed(client.getClientSocket());
        if (!isAllowed) {
            mts = Feedback.handshake(client, MessageType.reject, "Your ip is not allowed to connect");
            out.writeUTF(mts.getData());
            return false;
        }

        String userId = mto.getId();
        client.setUserId(userId);
        boolean isUserIdUnique = resources.manageGlobalClientList(ProxyResources.CLIENT_ADD, client);
        if (!isUserIdUnique) {
            mts = Feedback.handshake(client, MessageType.reject, "Your userId is used already by other user");
            out.writeUTF(mts.getData());
            return false;
        }

        mts = Feedback.handshake(client, MessageType.acknowledge, "Successfully connected");
        out.writeUTF(mts.getData());
        return true;
    }

    private boolean isIpAllowed(Socket clientSocket) {
        String ipAddress = clientSocket.getInetAddress().getHostAddress();

        String[] allowedIPAddresses = ProxyConfig.getConfig().getAllowedAddresses();
        if (allAddressesListened(allowedIPAddresses)) return true;

        for (String allowedRange : allowedIPAddresses) {
            SubnetUtils utils = new SubnetUtils(allowedRange);
            utils.setInclusiveHostCount(true);
            if (utils.getInfo().isInRange(ipAddress)) {
                return true;
            }
        }
        return false;
    }

    private boolean allAddressesListened(String[] allowedIPAddresses) {
        return allowedIPAddresses.length == 1 && allowedIPAddresses[0].equals("*");
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
