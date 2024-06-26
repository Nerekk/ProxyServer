package org.example.proxyserver.Proxy;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

@Getter
@Setter
public class Client {
    private String userId;

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public Client(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
    }
}
