package org.maverick.middletalkclient.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.maverick.middletalkclient.NavigationController;
import org.maverick.middletalkclient.State;
import org.maverick.middletalkclient.models.Conference;
import org.maverick.middletalkclient.services.DataManager;

import java.io.IOException;

public class LobbyController {

    @FXML private ComboBox<Conference> conferenceCombo;

    @FXML public void initialize() throws IOException, InterruptedException {

        DataManager.getConferenceList();

        conferenceCombo.setItems(
                FXCollections.observableList(State.getConferences())
        );

        conferenceCombo.setDisable(false);

    }

    @FXML public void onConnectButtonClick() {

        State.setCurrentConference(conferenceCombo.getValue());

        NavigationController.navigateTo("chat-view.fxml");

    }

    public void onCreateConferenceButtonClick() {

        NavigationController.navigateTo("create-conference-view.fxml");

    }
}
