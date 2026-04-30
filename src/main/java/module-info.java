module org.maverick.middletalkclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.maverick.middletalkclient to javafx.fxml;
    exports org.maverick.middletalkclient;
}