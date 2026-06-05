module com.example.volleyballrotations {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;


    opens com.volleyballrotations.frontend to javafx.fxml;
    opens com.volleyballrotations.backend to javafx.fxml;
    exports com.volleyballrotations.backend;
    exports com.volleyballrotations.frontend;
    opens com.volleyballrotations.frontend.controllers to javafx.fxml;
}