package org.example.proxyserver.Threads;

import org.example.proxyserver.Proxy.Com_resources.ProxyResources;
import org.example.proxyserver.Proxy.Config.ProxyConfig;
import org.example.proxyserver.Proxy.Monitoring_utils.MessageManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class MonitoringThread extends Thread {

    private ProxyConfig config;
    private final ProxyResources resources;
    private final MessageManager manager;
    private AtomicBoolean STOP_SERVER;

    public MonitoringThread(ProxyConfig config, ProxyResources resources, AtomicBoolean STOP_SERVER) {
        this.config = config;
        this.resources = resources;
        this.STOP_SERVER = STOP_SERVER;
        this.manager = new MessageManager(resources);

        start();
    }

    public void run() {

        while (!STOP_SERVER.get()) {
            if (resources.getReceiveQueue().isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            manager.handleMessageReceived();
        }
    }


}
