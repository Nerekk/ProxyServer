package org.example.proxyserver.Proxy.Config;

import org.example.proxyserver.Proxy.Config.Exceptions.ConfigException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigValidator {
    private static final String IPv4_PATTERN =
            "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    private static final String IPv4_CIDR_PATTERN =
            "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\/(3[0-2]|[12]?[0-9])\\b";

    public static boolean isValidIPv4(String ip) {
        Pattern pattern = Pattern.compile(IPv4_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean isValidIPv4CIDR(String cidr) {
        Pattern pattern = Pattern.compile(IPv4_CIDR_PATTERN);
        Matcher matcher = pattern.matcher(cidr);
        return matcher.matches();
    }

    public static boolean allAddressesListened(String[] listenAddresses) {
        return listenAddresses.length == 1 && listenAddresses[0].equals("*");
    }

    public static void validateFields(String[] fields, ProxyConfig config) throws ConfigException {
        JSONObject jsonObject = config.getJsonData();

        for (String field : fields) {
            if (!jsonObject.has(field))
                throw new ConfigException(field + " is missing.");

            if (field.equals(
                    ProxyConfig.LISTEN_ADDRESSES) &&
                    !validateListenedAddresses(config.getListenAddresses())) {

                throw new ConfigException(ProxyConfig.LISTEN_ADDRESSES + " are invalid");
            }

            if (field.equals(
                    ProxyConfig.ALLOWED_IP_ADDRESSES) &&
                    !validateAllowedAddresses(config.getAllowedAddresses())) {

                throw new ConfigException(ProxyConfig.ALLOWED_IP_ADDRESSES + " are invalid");
            }
        }
    }

    private static boolean validateListenedAddresses(String[] addresses) {
        if (allAddressesListened(addresses)) return true;

        for (String address : addresses) {
            if (!isValidIPv4(address)) return false;
        }

        return true;
    }

    private static boolean validateAllowedAddresses(String[] addresses) {
        if (allAddressesListened(addresses)) return true;

        for (String address : addresses) {
            if (!isValidIPv4CIDR(address)) return false;
        }

        return true;
    }
}
