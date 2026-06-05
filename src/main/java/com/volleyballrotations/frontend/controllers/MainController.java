package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.League;
import com.volleyballrotations.backend.Lineup;
import com.volleyballrotations.backend.Team;
import com.volleyballrotations.frontend.VolleyballApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {
    @FXML private ListView<Team> teamList;
    @FXML private ListView<Lineup> lineupList;
    @FXML private StackPane centerPane;

    public void initialize() {
        teamList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, team) -> {
                    if (team != null) {
                        lineupList.setItems(team.lineupList());
                        loadTeam(team);
                    }
                }
        );
        lineupList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, lineup) -> {
                    if (lineup != null) loadCourt(lineup);
                }
        );
    }

    public void setLeague(League league) {
        teamList.setItems(league.getTeams());
    }

    private void loadTeam(Team team) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    VolleyballApplication.class.getResource("team-view.fxml"));
            Parent view = loader.load();
            TeamController ctrl = loader.getController();
            ctrl.setTeam(team);
            centerPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCourt(Lineup lineup) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    VolleyballApplication.class.getResource("court-view.fxml"));
            Parent view = loader.load();
            CourtController ctrl = loader.getController();
            ctrl.setLineup(lineup);
            centerPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

