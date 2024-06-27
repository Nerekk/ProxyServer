package org.example.proxyserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.proxyserver.Gui.Logger;
import org.example.proxyserver.Proxy.ProxyServerManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXController implements Initializable {
    @FXML
    private TextArea topicsText, loggerText, serversText;

    @FXML
    private Button startButton, stopButton, refreshButton;

    public Logger logger;

    private ProxyServerManager manager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.logger = new Logger(topicsText, loggerText, serversText);
        Logger.setInstance(this.logger);
    }

    @FXML
    protected void start() {
        loggerText.clear();
        logger.log(Logger.INFO, "Starting main server..");
        try {
            manager = new ProxyServerManager(this);
            manager.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        swapButtonStatus();
    }

    @FXML
    protected void stop() {
        manager.stop();
        logger.log(Logger.INFO, "Main server stopped.");

        swapButtonStatus();
    }

    @FXML
    protected void refresh() {
        manager.refreshGui();
        logger.log(Logger.INFO, "Refreshed");
    }

    private void swapButtonStatus() {
        if (startButton.isDisabled()) {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            refreshButton.setDisable(true);
        } else {
            startButton.setDisable(true);
            stopButton.setDisable(false);
            refreshButton.setDisable(false);
        }
    }
}