module com.example.volleyballrotations {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;


    opens com.volleyballrotations.frontend to javafx.fxml;
    exports com.volleyballrotations.frontend;
}