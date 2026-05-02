package org.maverick.middletalkclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.maverick.middletalkclient.NavigationController;
import org.maverick.middletalkclient.services.DataManager;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label statusLabel;

    @FXML
    protected void onAuthButtonClick() {
        try {

            DataManager.login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            NavigationController.navigateTo("lobby-view.fxml");

        } catch (Exception e) {

            statusLabel.setText(e.toString());

        }
    }
}
