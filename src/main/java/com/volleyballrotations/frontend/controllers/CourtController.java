package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.Lineup;
import com.volleyballrotations.backend.Rotation;
import com.volleyballrotations.backend.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class CourtController {
    @FXML private ListView<Rotation> rotationList;
    @FXML private ListView<State> stateList;
    @FXML private Pane courtPane;

    private Lineup lineup;

    public void initialize() {
        rotationList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Rotation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Rotation " + (getIndex() + 1));
            }
        });

        stateList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(State item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        rotationList.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, rot) -> {
                if (rot != null) stateList.setItems(rot.stateList());
            }
        );

        stateList.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, state) -> {
                if (state != null) drawCourt(state);
            }
        );
    }

    public void setLineup(Lineup lineup) {
        this.lineup = lineup;
        rotationList.getItems().setAll(lineup.rotationList());
        rotationList.getSelectionModel().selectFirst();
    }

    @FXML private void onAddState() {
        Rotation rot = rotationList.getSelectionModel().getSelectedItem();
        State current = stateList.getSelectionModel().getSelectedItem();
        if (rot == null || current == null) return;
        rot.stateList().add(current.copy());
    }

    private void drawCourt(State state) {
        courtPane.getChildren().clear();
        // TODO: draw player circles for each Position in state.positionList()
    }
}
