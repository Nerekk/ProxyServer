package org.example.proxyserver.Threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private volatile boolean STOP_CLIENT;

    public ClientHandler(Socket clientSocket, boolean STOP_CLIENT) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());

        this.STOP_CLIENT = STOP_CLIENT;

    }

    public void run() {
        System.out.println("### [" + clientSocket.getInetAddress() + "] Handling client..");

        while (!STOP_CLIENT) {

        }

    }
}
