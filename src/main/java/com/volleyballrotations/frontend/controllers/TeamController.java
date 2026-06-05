package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.Player;
import com.volleyballrotations.backend.Team;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class TeamController {
    @FXML private TextField              teamNameField;
    @FXML private ColorPicker            colorPicker;
    @FXML private TableView<Player>      rosterTable;
    @FXML private TableColumn<Player, Number> numberCol;
    @FXML private TableColumn<Player, String> nameCol;
    @FXML private TableColumn<Player, String> posCol;
    @FXML private TextField addName, addNumber, addPos;

    private Team team;

    public void initialize() {
        numberCol.setCellValueFactory(d -> d.getValue().numberProperty());
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
        posCol.setCellValueFactory(d -> d.getValue().defaultPosProperty());
    }

    public void setTeam(Team team) {
        this.team = team;
        teamNameField.setText(team.getName());
        rosterTable.setItems(team.getRoster().playerList());
        try {
            colorPicker.setValue(Color.web(team.getColor()));
        } catch (IllegalArgumentException e) {
            colorPicker.setValue(Color.web("#4488ff"));
        }
    }

    @FXML private void onRename() {
        String n = teamNameField.getText().trim();
        if (!n.isEmpty()) team.setName(n);
    }

    @FXML private void onColorChange() {
        Color c = colorPicker.getValue();
        team.setColor(String.format("#%02x%02x%02x",
                (int) Math.round(c.getRed()   * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue()  * 255)));
    }

    @FXML private void onAddPlayer() {
        String name   = addName.getText().trim();
        String numStr = addNumber.getText().trim();
        String pos    = addPos.getText().trim();
        if (name.isEmpty() || numStr.isEmpty()) return;
        try {
            team.getRoster().addPlayer(name, Integer.parseInt(numStr), pos);
            addName.clear();
            addNumber.clear();
            addPos.clear();
        } catch (NumberFormatException ignored) {}
    }

    @FXML private void onRemovePlayer() {
        Player p = rosterTable.getSelectionModel().getSelectedItem();
        if (p != null) team.getRoster().playerList().remove(p);
    }
}
