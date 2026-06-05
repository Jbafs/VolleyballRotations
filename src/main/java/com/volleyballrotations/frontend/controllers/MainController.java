package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.League;
import com.volleyballrotations.backend.Lineup;
import com.volleyballrotations.backend.Player;
import com.volleyballrotations.backend.Team;
import com.volleyballrotations.frontend.DataManager;
import com.volleyballrotations.frontend.VolleyballApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML private ListView<Team>   teamList;
    @FXML private ListView<Lineup> lineupList;
    @FXML private ListView<Team>   awayTeamList;
    @FXML private ListView<Lineup> awayLineupList;
    @FXML private StackPane        centerPane;

    private League          league;
    private CourtController currentCourtCtrl;
    private Team            homeTeam, awayTeam;
    private Lineup          homeLineup, awayLineup;

    public void initialize() {
        teamList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, team) -> {
                    if (team != null) {
                        homeTeam = team;
                        lineupList.setItems(team.lineupList());
                        currentCourtCtrl = null;
                        loadTeam(team);
                    }
                }
        );
        lineupList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, lineup) -> {
                    if (lineup != null) {
                        homeLineup = lineup;
                        loadCourt();
                    }
                }
        );
        awayTeamList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, team) -> {
                    if (team != null) {
                        awayTeam = team;
                        awayLineupList.setItems(team.lineupList());
                    }
                }
        );
        awayLineupList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, lineup) -> {
                    awayLineup = lineup;
                    if (currentCourtCtrl != null && lineup != null) {
                        currentCourtCtrl.setAwayLineup(lineup);
                        if (awayTeam != null) currentCourtCtrl.setAwayColor(awayTeam.getColor());
                    }
                }
        );
    }

    public void setLeague(League league) {
        this.league = league;
        teamList.setItems(league.getTeams());
        awayTeamList.setItems(league.getTeams());
    }

    @FXML private void onSave() {
        try { DataManager.save(league); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void onAddTeam() {
        league.getTeams().add(new Team("New Team"));
    }

    @FXML private void onAddLineup() {
        Team team = teamList.getSelectionModel().getSelectedItem();
        if (team == null || team.getRoster().playerList().size() < 6) return;
        Player[] players = team.getRoster().playerList().subList(0, 6).toArray(new Player[0]);
        team.lineupList().add(new Lineup(players));
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

    private void loadCourt() {
        if (homeLineup == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    VolleyballApplication.class.getResource("court-view.fxml"));
            Parent view = loader.load();
            currentCourtCtrl = loader.getController();
            currentCourtCtrl.setHomeLineup(homeLineup);
            if (homeTeam != null) currentCourtCtrl.setHomeColor(homeTeam.getColor());
            if (awayLineup != null) {
                currentCourtCtrl.setAwayLineup(awayLineup);
                if (awayTeam != null) currentCourtCtrl.setAwayColor(awayTeam.getColor());
            }
            centerPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
