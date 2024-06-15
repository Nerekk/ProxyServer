module org.example.proxyserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.proxyserver to javafx.fxml;
    exports org.example.proxyserver;
}