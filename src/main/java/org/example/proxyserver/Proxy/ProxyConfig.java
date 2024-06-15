package org.example.proxyserver.Proxy;


import org.example.proxyserver.Proxy.Exceptions.ValidationException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProxyConfig {
    public static final String SERVER_ID = "ServerID";
    public static final String LISTEN_ADDRESSES = "ListenAddresses";
    public static final String LISTEN_PORT = "ListenPort";
    public static final String TIME_OUT = "TimeOut";
    public static final String SIZE_LIMIT = "SizeLimit";
    public static final String ALLOWED_IP_ADDRESSES = "AllowedIPAdresses";

    private final JSONObject jsonData;

    private ProxyConfig(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public static ProxyConfig getConfig(String filename) throws IOException, ValidationException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));

        JSONObject json = new JSONObject(content);
        validate(json);
        return new ProxyConfig(json);
    }

    public Object getValue(String key) {
        return jsonData.get(key);
    }

    private static void validate(JSONObject jsonObject) throws ValidationException {
        String[] fields = {SERVER_ID, LISTEN_ADDRESSES, LISTEN_PORT, TIME_OUT, SIZE_LIMIT, ALLOWED_IP_ADDRESSES};

        for (String field : fields) {
            if (!jsonObject.has(field))
                throw new ValidationException(field + " is missing.");
        }
    }
}
