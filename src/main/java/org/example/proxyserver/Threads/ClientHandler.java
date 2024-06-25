package org.example.proxyserver.Threads;

import org.example.proxyserver.Enums.MessageMode;
import org.example.proxyserver.Enums.MessageType;
import org.example.proxyserver.Proxy.Client;
import org.example.proxyserver.Proxy.Com_resources.MessageReceived;
import org.example.proxyserver.Proxy.Com_resources.MessageToSend;
import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Com_resources.Transfer.MessageTransferObject;
import org.example.proxyserver.Proxy.Com_resources.Transfer.Payload;
import org.example.proxyserver.Proxy.Config.ProxyConfig;
import org.example.proxyserver.Proxy.Monitoring_utils.Feedback;
import org.example.proxyserver.Proxy.Utils.MTOJsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler extends Thread {
    private final Client client;
    private final DataInputStream in;
    private final ProxyResources resources;

    private final AtomicBoolean STOP_CLIENT;
    private boolean isClosed;

    public ClientHandler(Client client, ProxyResources resources, AtomicBoolean STOP_CLIENT) {
        this.client = client;
        this.resources = resources;
        this.STOP_CLIENT = STOP_CLIENT;

        this.in = new DataInputStream(this.client.getInputStream());
    }

    public void run() {
        System.out.println("### [" + client.getClientSocket().getInetAddress() + "] Handling client..");
        isClosed = false;
        while (!STOP_CLIENT.get()) {
            String jsonData;
            try {
                jsonData = in.readUTF();

                if (isMessageTooBig(jsonData)) {
                    sendRejectToClient();
                    continue;
                }

                System.out.println(jsonData);
            } catch (IOException _) {
                // empty because of socket timeout
                continue;
            }

            if (!jsonData.isEmpty()) {
                if (isDisconnecting(jsonData)) {
                    closeCallByClient();
                    isClosed = true;
                    break;
                }
                resources.addMessageReceived(new MessageReceived(client, jsonData));
            }
        }

        if (!isClosed) {
            closeCallByServer();
        }
        System.out.println("### [" + client.getClientSocket().getInetAddress() + "] Handling ended");
    }

    private boolean isMessageTooBig(String message) {
        return message.getBytes().length > ProxyConfig.getConfig().getSizeLimit();
    }

    private void sendRejectToClient() {
        MessageTransferObject mto = new MessageTransferObject(MessageType.reject, client.getUserId(), "Size", MessageMode.producer);
        mto.setPayload(new Payload());
        MessageReceived mr = new MessageReceived(client, MTOJsonParser.parseToString(mto));
        MessageToSend mts = Feedback.getReject(mr, "Message size is too big");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        try {
            out.writeUTF(mts.getData());
        } catch (IOException e) {
            System.out.println("CLIENT EXCEPTION");
        }
    }

    private void closeCallByClient() {
        resources.manageGlobalClientList(ProxyResources.CLIENT_REMOVE, client);

        resources.removeClientData(client);
        try {
            client.getOutputStream().close();
            client.getInputStream().close();
            client.getClientSocket().close();
        } catch (IOException e) {
            System.out.println("CLIENT EXCEPTION");
        }
    }
    private void closeCallByServer() {
        MessageToSend mts = Feedback.disconnect(client);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        try {
            out.writeUTF(mts.getData());

            resources.manageGlobalClientList(ProxyResources.CLIENT_REMOVE, client);

            client.getOutputStream().close();
            client.getInputStream().close();
            client.getClientSocket().close();
        } catch (IOException e) {
            System.out.println("CLIENT EXCEPTION");
        }
    }

    private boolean isDisconnecting(String jsonData) {
        MessageTransferObject mto = MTOJsonParser.parseJsonToMessageTransferObject(jsonData);

        return mto.getType() == MessageType.status && mto.getPayload().getTopicOfMessage().equals("Disconnect");
    }
}
