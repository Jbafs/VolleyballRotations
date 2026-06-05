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
        League league;
        try {
            League loaded = DataManager.load();
            league = (loaded != null) ? loaded : League.createWithDefaults();
        } catch (IOException e) {
            e.printStackTrace();
            league = League.createWithDefaults();
        }

        FXMLLoader loader = new FXMLLoader(VolleyballApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 650);

        MainController ctrl = loader.getController();
        ctrl.setLeague(league);

        final League leagueRef = league;
        stage.setOnCloseRequest(e -> {
            try { DataManager.save(leagueRef); } catch (IOException ex) { ex.printStackTrace(); }
        });

        stage.setTitle("Volleyball Rotations");
        stage.setScene(scene);
        stage.show();
    }
}
