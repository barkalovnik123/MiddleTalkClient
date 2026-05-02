module org.maverick.middletalkclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires org.java_websocket;
    requires org.fxmisc.richtext;


    opens org.maverick.middletalkclient to javafx.fxml;
    opens org.maverick.middletalkclient.models to com.fasterxml.jackson.databind;
    exports org.maverick.middletalkclient;
    exports org.maverick.middletalkclient.models;
    exports org.maverick.middletalkclient.controllers;
    opens org.maverick.middletalkclient.controllers to javafx.fxml;
}