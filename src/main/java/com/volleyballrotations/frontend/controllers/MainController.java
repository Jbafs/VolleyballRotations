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
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Root controller for the main window, managing team/lineup selection and switching the center pane
 * between the team editor and the court view.
 */
public class MainController {
    @FXML private ListView<Team>   teamList;
    @FXML private ListView<Lineup> lineupList;
    @FXML private TextField        lineupNameField;
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
                if (old    != null) lineupNameField.textProperty().unbindBidirectional(old.nameProperty());
                if (lineup != null) {
                    homeLineup = lineup;
                    lineupNameField.textProperty().bindBidirectional(lineup.nameProperty());
                    loadCourt();
                } else {
                    lineupNameField.clear();
                }
            }
        );
        awayTeamList.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, team) -> {
                if (team != null) {
                    awayTeam = team;
                    awayLineupList.setItems(team.lineupList());
                    if (currentCourtCtrl != null) currentCourtCtrl.setAwayTeam(team);
                }
            }
        );
        awayLineupList.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, lineup) -> {
                awayLineup = lineup;
                if (currentCourtCtrl != null && lineup != null) {
                    currentCourtCtrl.setAwayLineup(lineup);
                    if (awayTeam != null) {
                        currentCourtCtrl.setAwayColor(awayTeam.getColor());
                        currentCourtCtrl.setAwayTeam(awayTeam);
                    }
                }
            }
        );
    }

    /** Binds the league's team list to both the home and away team selectors. */
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

    @FXML private void onRemoveLineup() {
        Lineup lineup = lineupList.getSelectionModel().getSelectedItem();
        Team   team   = teamList.getSelectionModel().getSelectedItem();
        if (lineup != null && team != null) team.lineupList().remove(lineup);
    }

    @FXML private void onAddLineup() {
        Team team = teamList.getSelectionModel().getSelectedItem();
        if (team == null || team.getRoster().playerList().size() < 6) return;
        Player[] players = team.getRoster().playerList().subList(0, 6).toArray(new Player[0]);
        team.lineupList().add(new Lineup(players));
    }

    /** Loads and displays the team-editor view for the given team in the center pane. */
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

    /** Loads the court view for the currently selected home lineup, injecting away data if available. */
    private void loadCourt() {
        if (homeLineup == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    VolleyballApplication.class.getResource("court-view.fxml"));
            Parent view = loader.load();
            currentCourtCtrl = loader.getController();
            currentCourtCtrl.setHomeLineup(homeLineup);
            if (homeTeam != null) {
                currentCourtCtrl.setHomeColor(homeTeam.getColor());
                currentCourtCtrl.setHomeTeam(homeTeam);
            }
            if (awayLineup != null) {
                currentCourtCtrl.setAwayLineup(awayLineup);
                if (awayTeam != null) {
                    currentCourtCtrl.setAwayColor(awayTeam.getColor());
                    currentCourtCtrl.setAwayTeam(awayTeam);
                }
            }
            centerPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
