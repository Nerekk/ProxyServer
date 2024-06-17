package org.example.proxyserver.Proxy;


import org.example.proxyserver.Proxy.Exceptions.ConfigException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProxyConfig {
    public static final String FILENAME = "config.json";

    public static final String SERVER_ID = "ServerID";
    public static final String LISTEN_ADDRESSES = "ListenAddresses";
    public static final String LISTEN_PORT = "ListenPort";
    public static final String TIME_OUT = "TimeOut";
    public static final String SIZE_LIMIT = "SizeLimit";
    public static final String ALLOWED_IP_ADDRESSES = "AllowedIPAdresses";

    private static volatile ProxyConfig config;
    private final JSONObject jsonData;

    private ProxyConfig(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public static ProxyConfig getConfig() throws IOException, ConfigException {
        ProxyConfig result = config;
        if (result != null) {
            return result;
        }
        synchronized(ProxyConfig.class) {
            if (config == null) {
                String content = new String(Files.readAllBytes(Paths.get(FILENAME)));

                JSONObject json = new JSONObject(content);

                config = new ProxyConfig(json);
                validate(config);
            }
            return config;
        }
    }

    private static void validate(ProxyConfig config) throws ConfigException {
        String[] fields = {SERVER_ID, LISTEN_ADDRESSES, LISTEN_PORT, TIME_OUT, SIZE_LIMIT, ALLOWED_IP_ADDRESSES};
        ConfigValidator.validateFields(fields, config);
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public Object getValue(String key) {
        return jsonData.get(key);
    }

    public String getServerId() {
        return jsonData.getString(SERVER_ID);
    }

    public int getListenedPort() {
        return jsonData.getInt(LISTEN_PORT);
    }

    public int getTimeOut() {
        return jsonData.getInt(TIME_OUT);
    }

    public int getSizeLimit() {
        return jsonData.getInt(SIZE_LIMIT);
    }

    public String[] getListenAddresses() {
        JSONArray jsonArray = jsonData.getJSONArray(LISTEN_ADDRESSES);

        String[] addresses = parseToStringArray(jsonArray);

        return addresses;
    }

    public String[] getAllowedAddresses() {
        JSONArray jsonArray = jsonData.getJSONArray(ALLOWED_IP_ADDRESSES);

        String[] addresses = parseToStringArray(jsonArray);

        return addresses;
    }

    private static String[] parseToStringArray(JSONArray jsonArray) {
        String[] addresses = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            addresses[i] = jsonArray.getString(i);
        }
        return addresses;
    }

}
