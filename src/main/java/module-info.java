module org.example.proxyserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires static lombok;
    requires com.google.gson;


    opens org.example.proxyserver to javafx.fxml;
    exports org.example.proxyserver;
    exports org.example.proxyserver.Proxy;
    opens org.example.proxyserver.Proxy to javafx.fxml;
    exports org.example.proxyserver.Threads;
    opens org.example.proxyserver.Threads to javafx.fxml;
    exports org.example.proxyserver.Proxy.Config;
    opens org.example.proxyserver.Proxy.Config to javafx.fxml;
    exports org.example.proxyserver.Proxy.Com_resources;
    opens org.example.proxyserver.Proxy.Com_resources to javafx.fxml;
    exports org.example.proxyserver.Proxy.Com_resources.Transfer;
    opens org.example.proxyserver.Proxy.Com_resources.Transfer to javafx.fxml, com.google.gson;
    exports org.example.proxyserver.Proxy.Utils;
    opens org.example.proxyserver.Proxy.Utils to javafx.fxml;
    exports org.example.proxyserver.Proxy.Monitoring_utils;
    opens org.example.proxyserver.Proxy.Monitoring_utils to javafx.fxml;
    exports org.example.proxyserver.Proxy.Monitoring_utils.Types;
    opens org.example.proxyserver.Proxy.Monitoring_utils.Types to javafx.fxml;
    exports org.example.proxyserver.Enums;
    opens org.example.proxyserver.Enums to com.google.gson;
}