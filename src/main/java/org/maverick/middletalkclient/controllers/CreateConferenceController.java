package org.maverick.middletalkclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.maverick.middletalkclient.NavigationController;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.services.CreateConferenceService;

public class CreateConferenceController {

    @FXML public TextField nameInput;
    @FXML public TextArea descriptionArea;

    public void onBackButtonClick() {

        NavigationController.navigateTo("lobby-view.fxml");

    }

    public void onCreateConferenceButtonClick(ActionEvent actionEvent) {

        CreateConferenceService.create(
                nameInput.getText(),
                descriptionArea.getText(),
                State.getUser().id()
        );

        NavigationController.navigateTo("lobby-view.fxml");

    }
}
