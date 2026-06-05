package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.Lineup;
import com.volleyballrotations.backend.Position;
import com.volleyballrotations.backend.Rotation;
import com.volleyballrotations.backend.State;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CourtController {
    @FXML private ListView<Rotation> homeRotationList;
    @FXML private ListView<Rotation> awayRotationList;
    @FXML private ListView<State>    homeStateList;
    @FXML private ListView<State>    awayStateList;
    @FXML private Pane               courtPane;

    private Lineup homeLineup, awayLineup;
    private State  currentHomeState, currentAwayState;
    private Color  homeColor = Color.web("#4488ff");
    private Color  awayColor = Color.web("#cc3333");

    private static final double RADIUS  = 20;
    private static final double PADDING = 16;

    public void initialize() {
        initLists(homeRotationList, homeStateList, true);
        initLists(awayRotationList, awayStateList, false);
        courtPane.widthProperty().addListener((obs, old, w) -> redraw());
        courtPane.heightProperty().addListener((obs, old, h) -> redraw());
    }

    private void initLists(ListView<Rotation> rots, ListView<State> stats, boolean isHome) {
        rots.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Rotation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Rotation " + (getIndex() + 1));
            }
        });
        stats.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(State item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        rots.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, rot) -> {
                    if (rot != null) {
                        stats.setItems(rot.stateList());
                        stats.getSelectionModel().selectFirst();
                    }
                }
        );
        stats.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, state) -> {
                    if (isHome) currentHomeState = state;
                    else        currentAwayState = state;
                    redraw();
                }
        );
    }

    public void setHomeLineup(Lineup lineup) {
        this.homeLineup = lineup;
        homeRotationList.getItems().setAll(lineup.rotationList());
        homeRotationList.getSelectionModel().selectFirst();
    }

    public void setAwayLineup(Lineup lineup) {
        this.awayLineup = lineup;
        awayRotationList.getItems().setAll(lineup.rotationList());
        awayRotationList.getSelectionModel().selectFirst();
    }

    public void setHomeColor(String hex) {
        try { homeColor = Color.web(hex); } catch (IllegalArgumentException ignored) {}
        redraw();
    }

    public void setAwayColor(String hex) {
        try { awayColor = Color.web(hex); } catch (IllegalArgumentException ignored) {}
        redraw();
    }

    @FXML private void onAddStateHome() {
        Rotation rot     = homeRotationList.getSelectionModel().getSelectedItem();
        State    current = homeStateList.getSelectionModel().getSelectedItem();
        if (rot == null || current == null) return;
        rot.stateList().add(current.copy());
    }

    @FXML private void onAddStateAway() {
        Rotation rot     = awayRotationList.getSelectionModel().getSelectedItem();
        State    current = awayStateList.getSelectionModel().getSelectedItem();
        if (rot == null || current == null) return;
        rot.stateList().add(current.copy());
    }

    private void redraw() {
        drawCourt(currentHomeState, currentAwayState);
    }

    private void drawCourt(State homeState, State awayState) {
        courtPane.getChildren().clear();
        double w = courtPane.getWidth();
        double h = courtPane.getHeight();
        if (w <= 0 || h <= 0) return;

        double pad        = PADDING;
        double half       = w / 2.0;
        double courtW     = w - 2 * pad;
        double courtH     = h - 2 * pad;
        double halfCourtW = half - pad;

        // Court boundary
        Rectangle boundary = new Rectangle(pad, pad, courtW, courtH);
        boundary.setFill(Color.TRANSPARENT);
        boundary.setStroke(Color.WHITE);
        boundary.setStrokeWidth(2);

        // Net — vertical line at center
        Line net = new Line(half, pad, half, h - pad);
        net.setStroke(Color.WHITE);
        net.setStrokeWidth(3);

        // Attack lines — 3m from net, half-court depth is 9m → 1/3 of half
        double attackDist = halfCourtW / 3.0;
        Line homeAttack = new Line(half - attackDist, pad, half - attackDist, h - pad);
        Line awayAttack = new Line(half + attackDist, pad, half + attackDist, h - pad);
        for (Line l : new Line[]{homeAttack, awayAttack}) {
            l.setStroke(Color.WHITE);
            l.setStrokeWidth(1);
            l.getStrokeDashArray().addAll(8.0, 5.0);
        }

        courtPane.getChildren().addAll(boundary, net, homeAttack, awayAttack);

        // --- Player tokens ---
        // Coordinate system: x = sideline-to-sideline (0=left, 1=right from team's POV)
        //                    y = depth (0=near net, 1=back line)
        //
        // Home faces right (left half):
        //   pixelX = pad + (1 - y) * halfCourtW   ← b = rightmost of home half
        //   pixelY = pad + x * courtH
        //
        // Away faces left / mirrored (right half):
        //   pixelX = half + y * halfCourtW         ← y=0 at net = leftmost of away half
        //   pixelY = pad + (1 - x) * courtH        ← x-axis mirrored so players face each other

        if (homeState != null) {
            for (Position p : homeState.positionList()) {
                double px = pad + p.getYPos() * halfCourtW;
                double py = pad + p.getXPos() * courtH;
                courtPane.getChildren().add(makeToken(p, px, py, homeColor));
            }
        }
        if (awayState != null) {
            for (Position p : awayState.positionList()) {
                double px = half + (1.0 - p.getYPos()) * halfCourtW;
                double py = pad + (1.0 - p.getXPos()) * courtH;
                courtPane.getChildren().add(makeToken(p, px, py, awayColor));
            }
        }
    }

    private StackPane makeToken(Position p, double cx, double cy, Color teamColor) {
        Circle circle = new Circle(RADIUS);
        circle.setFill(teamColor);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(1.5);

        Text label = new Text(String.valueOf(p.getNumber()));
        label.setFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        StackPane token = new StackPane(circle, label);
        token.setLayoutX(cx - RADIUS);
        token.setLayoutY(cy - RADIUS);
        return token;
    }
}
