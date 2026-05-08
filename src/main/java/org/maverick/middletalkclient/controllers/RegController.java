package org.maverick.middletalkclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.maverick.middletalkclient.NavigationController;
import org.maverick.middletalkclient.exceptions.AuthError;
import org.maverick.middletalkclient.services.ApiClient;

import java.io.IOException;

public class RegController {

    @FXML public Label statusLabel;
    @FXML public TextField usernameField;
    @FXML public TextField passwordField;

    @FXML public void onRegisterButtonClick() {

        try {
            ApiClient.sendRegisterRequest(usernameField.getText(), passwordField.getText());
            NavigationController.navigateTo("auth-view-modern.fxml");
        } catch (InterruptedException | IOException | AuthError e) {
            statusLabel.setText(e.toString());
        }

    }
}
