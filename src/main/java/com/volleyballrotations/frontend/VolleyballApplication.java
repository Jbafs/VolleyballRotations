package com.volleyballrotations.frontend;

import java.io.IOException;

import com.volleyballrotations.backend.League;
import com.volleyballrotations.frontend.controllers.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VolleyballApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        League league = new League();

        FXMLLoader loader = new FXMLLoader(VolleyballApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        MainController ctrl = loader.getController();
        ctrl.setLeague(league);

        stage.setTitle("Volleyball Rotations");
        stage.setScene(scene);
        stage.show();
    }

}
