package org.maverick.middletalkclient;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.stage.Stage;

public class MultiLoader {



    public static void main() {

        String[] args = new String[0];
        for (byte i = 0; i < 2; i++)
            Launcher.main(args);
    }

}
