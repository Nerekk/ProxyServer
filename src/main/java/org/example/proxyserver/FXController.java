package org.example.proxyserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.proxyserver.Proxy.Exceptions.ConfigException;
import org.example.proxyserver.Proxy.ProxyConfig;
import org.example.proxyserver.Proxy.ProxyServerManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    private ProxyConfig config;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            config = ProxyConfig.getConfig();

            ProxyServerManager manager = new ProxyServerManager(this, config);

            manager.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}