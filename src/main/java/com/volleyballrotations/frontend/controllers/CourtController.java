package com.volleyballrotations.frontend.controllers;

import com.volleyballrotations.backend.Lineup;
import com.volleyballrotations.backend.Player;
import com.volleyballrotations.backend.Position;
import com.volleyballrotations.backend.Rotation;
import com.volleyballrotations.backend.State;
import com.volleyballrotations.backend.Team;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;


/**
 * Controller for the court view, rendering both teams' player tokens on a scaled court canvas
 * and handling drag-to-reposition and click-to-edit interactions.
 */
public class CourtController {
    @FXML private ListView<Rotation> homeRotationList;
    @FXML private ListView<Rotation> awayRotationList;
    @FXML private ListView<State>    homeStateList;
    @FXML private ListView<State>    awayStateList;
    @FXML private TextField          homeStateNameField;
    @FXML private TextField          awayStateNameField;
    @FXML private ToggleButton       zoneToggle;
    @FXML private ToggleButton       showNamesToggle;
    @FXML private ToggleButton       showRolesToggle;
    @FXML private ToggleButton       showRotationToggle;
    @FXML private Slider             radiusSlider;
    @FXML private StackPane          courtContainer;
    @FXML private Pane               courtPane;

    private Team   homeTeam,   awayTeam;
    private State  currentHomeState, currentAwayState;
    private Color  homeColor = Color.web("#4488ff");
    private Color  awayColor = Color.web("#cc3333");
    private Popup  editPopup;

    private static final double PADDING = 16;

    public void initialize() {
        var width  = Bindings.min(courtContainer.widthProperty(),
                                  courtContainer.heightProperty().multiply(2));
        var height = width.divide(2);
        courtPane.prefWidthProperty().bind(width);
        courtPane.prefHeightProperty().bind(height);
        courtPane.maxWidthProperty().bind(width);
        courtPane.maxHeightProperty().bind(height);

        initLists(homeRotationList, homeStateList, homeStateNameField, true);
        initLists(awayRotationList, awayStateList, awayStateNameField, false);
        courtPane.widthProperty().addListener((obs, old, w) -> redraw());
        courtPane.heightProperty().addListener((obs, old, h) -> redraw());
        radiusSlider.valueProperty().addListener((obs, old, v) -> redraw());
    }

    /**
     * Wires up the rotation and state list views with cell factories and selection listeners
     * so that picking a rotation populates the state list and selecting a state triggers a redraw.
     */
    private void initLists(ListView<Rotation> rots, ListView<State> stats,
                            TextField nameField, boolean isHome) {
        rots.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Rotation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Rotation " + (getIndex() + 1));
            }
        });
        stats.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(State item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { textProperty().unbind(); setText(null); }
                else                       { textProperty().bind(item.nameProperty()); }
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
                if (old   != null) nameField.textProperty().unbindBidirectional(old.nameProperty());
                if (state != null) nameField.textProperty().bindBidirectional(state.nameProperty());
                if (isHome) currentHomeState = state;
                else        currentAwayState = state;
                redraw();
            }
        );
    }

    /** Populates the home rotation list from the given lineup and selects the first rotation. */
    public void setHomeLineup(Lineup lineup) {
        homeRotationList.getItems().setAll(lineup.rotationList());
        homeRotationList.getSelectionModel().selectFirst();
    }

    /** Populates the away rotation list from the given lineup and selects the first rotation. */
    public void setAwayLineup(Lineup lineup) {
        awayRotationList.getItems().setAll(lineup.rotationList());
        awayRotationList.getSelectionModel().selectFirst();
    }

    /** Updates the home team's token color and redraws the court. */
    public void setHomeColor(String hex) {
        try { homeColor = Color.web(hex); } catch (IllegalArgumentException ignored) {}
        redraw();
    }

    /** Updates the away team's token color and redraws the court. */
    public void setAwayColor(String hex) {
        try { awayColor = Color.web(hex); } catch (IllegalArgumentException ignored) {}
        redraw();
    }

    public void setHomeTeam(Team team) { this.homeTeam = team; }
    public void setAwayTeam(Team team) { this.awayTeam = team; }

    @FXML private void onRedraw() { redraw(); }

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

    @FXML private void onDeleteStateHome() {
        Rotation rot   = homeRotationList.getSelectionModel().getSelectedItem();
        State    state = homeStateList.getSelectionModel().getSelectedItem();
        if (rot == null || state == null || rot.stateList().size() <= 1) return;
        rot.stateList().remove(state);
    }

    @FXML private void onDeleteStateAway() {
        Rotation rot   = awayRotationList.getSelectionModel().getSelectedItem();
        State    state = awayStateList.getSelectionModel().getSelectedItem();
        if (rot == null || state == null || rot.stateList().size() <= 1) return;
        rot.stateList().remove(state);
    }

    @FXML private void onCopyHomeToAway() {
        State    src    = homeStateList.getSelectionModel().getSelectedItem();
        Rotation target = awayRotationList.getSelectionModel().getSelectedItem();
        if (src == null || target == null || awayTeam == null) return;
        target.stateList().add(copyStateForTeam(src, awayTeam));
    }

    @FXML private void onCopyAwayToHome() {
        State    src    = awayStateList.getSelectionModel().getSelectedItem();
        Rotation target = homeRotationList.getSelectionModel().getSelectedItem();
        if (src == null || target == null || homeTeam == null) return;
        target.stateList().add(copyStateForTeam(src, homeTeam));
    }

    /** Copies a state's positions to a new state mapped onto the given team's roster players. */
    private State copyStateForTeam(State src, Team team) {
        var roster = team.getRoster().playerList();
        var srcPos = src.positionList();
        Position[] newPos = new Position[6];
        for (int i = 0; i < 6; i++) {
            Position sp = srcPos.get(i);
            Player   tp = i < roster.size() ? roster.get(i) : roster.get(roster.size() - 1);
            newPos[i] = new Position(sp.getXPos(), sp.getYPos(), tp, sp.getPositionName());
        }
        return new State(java.util.UUID.randomUUID().toString(), src.getName() + " Copy", newPos);
    }

    /** Hides any open edit popup and redraws both teams' current states on the court. */
    private void redraw() {
        if (editPopup != null) editPopup.hide();
        drawCourt(currentHomeState, currentAwayState);
    }

    /** Clears and repaints the court canvas with court lines, optional zone overlays, and player tokens. */
    private void drawCourt(State homeState, State awayState) {
        courtPane.getChildren().clear();
        double w = courtPane.getWidth();
        double h = courtPane.getHeight();
        if (w <= 0 || h <= 0) return;

        double pad    = PADDING;
        double half   = w / 2.0;
        double innerW = w - 2 * pad;
        double innerH = h - 2 * pad;
        double halfW  = half - pad;

        Rectangle boundary = new Rectangle(pad, pad, innerW, innerH);
        boundary.setFill(Color.TRANSPARENT);
        boundary.setStroke(Color.WHITE);
        boundary.setStrokeWidth(2);

        Line net = new Line(half, pad, half, h - pad);
        net.setStroke(Color.WHITE);
        net.setStrokeWidth(3);

        double attackDist = halfW / 3.0;
        Line homeAttack = new Line(half - attackDist, pad, half - attackDist, h - pad);
        Line awayAttack = new Line(half + attackDist, pad, half + attackDist, h - pad);
        for (Line l : new Line[]{homeAttack, awayAttack}) {
            l.setStroke(Color.WHITE);
            l.setStrokeWidth(1);
            l.getStrokeDashArray().addAll(8.0, 5.0);
        }
        courtPane.getChildren().addAll(boundary, net, homeAttack, awayAttack);

        // Zone overlay — bind to Position properties so lines track live drag updates
        int[][] pairs = {{0,1},{1,2},{2,3},{3,4},{4,5},{5,0},{2,5}};
        if (zoneToggle.isSelected() && homeState != null) {
            var hw = courtPane.widthProperty().divide(2).subtract(PADDING);
            var ih = courtPane.heightProperty().subtract(2 * PADDING);
            for (int[] pair : pairs) {
                Position p0 = homeState.positionList().get(pair[0]);
                Position p1 = homeState.positionList().get(pair[1]);
                Line zl = new Line();
                zl.startXProperty().bind(p0.yPosProperty().multiply(hw).add(PADDING));
                zl.startYProperty().bind(p0.xPosProperty().multiply(ih).add(PADDING));
                zl.endXProperty().bind(p1.yPosProperty().multiply(hw).add(PADDING));
                zl.endYProperty().bind(p1.xPosProperty().multiply(ih).add(PADDING));
                styleZoneLine(zl, homeColor);
                courtPane.getChildren().add(zl);
            }
        }
        if (zoneToggle.isSelected() && awayState != null) {
            var hn = courtPane.widthProperty().divide(2);
            var hw = hn.subtract(PADDING);
            var ih = courtPane.heightProperty().subtract(2 * PADDING);
            for (int[] pair : pairs) {
                Position p0 = awayState.positionList().get(pair[0]);
                Position p1 = awayState.positionList().get(pair[1]);
                Line zl = new Line();
                zl.startXProperty().bind(p0.yPosProperty().negate().add(1.0).multiply(hw).add(hn));
                zl.startYProperty().bind(p0.xPosProperty().negate().add(1.0).multiply(ih).add(PADDING));
                zl.endXProperty().bind(p1.yPosProperty().negate().add(1.0).multiply(hw).add(hn));
                zl.endYProperty().bind(p1.xPosProperty().negate().add(1.0).multiply(ih).add(PADDING));
                styleZoneLine(zl, awayColor);
                courtPane.getChildren().add(zl);
            }
        }

        if (homeState != null) {
            var ps = homeState.positionList();
            for (int i = 0; i < ps.size(); i++) {
                Position p = ps.get(i);
                double px = pad + p.getYPos() * halfW;
                double py = pad + p.getXPos() * innerH;
                courtPane.getChildren().add(makeToken(p, i + 1, px, py, homeColor, true));
            }
        }
        if (awayState != null) {
            var ps = awayState.positionList();
            for (int i = 0; i < ps.size(); i++) {
                Position p = ps.get(i);
                double px = half + (1.0 - p.getYPos()) * halfW;
                double py = pad  + (1.0 - p.getXPos()) * innerH;
                courtPane.getChildren().add(makeToken(p, i + 1, px, py, awayColor, false));
            }
        }
    }

    /** Applies a brightened, semi-transparent stroke to a zone overlay line using the team's base color. */
    private void styleZoneLine(Line zl, Color base) {
        zl.setStroke(base.deriveColor(0, 1.0, 1.4, 0.85));
        zl.setStrokeWidth(2.5);
    }

    /** Returns a Pane whose top-left sits at (cx-r, cy-r), so the circle center stays at (cx, cy). */
    private Pane makeToken(Position p, int rotNum, double cx, double cy, Color teamColor, boolean isHome) {
        double r = radiusSlider.getValue();
        boolean libero = p.getPlayer().getPos().equalsIgnoreCase("L");

        Circle circle = new Circle(r);
        circle.setFill(libero ? Color.WHITE : teamColor);
        circle.setStroke(libero ? teamColor : Color.WHITE);
        circle.setStrokeWidth(libero ? 3.0 : 1.5);

        Text numLabel = new Text(String.valueOf(p.getNumber()));
        numLabel.setFill(libero ? teamColor : Color.WHITE);
        numLabel.setFont(Font.font("Arial", FontWeight.BOLD, r * 0.65));

        StackPane badge = new StackPane(circle, numLabel);
        badge.setPrefSize(2 * r, 2 * r);
        badge.setMinSize(2 * r, 2 * r);
        badge.setMaxSize(2 * r, 2 * r);

        Pane container = new Pane(badge);

        if (showRotationToggle.isSelected()) {
            double cr = Math.max(r * 0.34, 7);
            Circle chip = new Circle(cr);
            chip.setFill(Color.web("#111"));
            chip.setStroke(Color.WHITE);
            chip.setStrokeWidth(1);
            Text rotText = new Text(String.valueOf(rotNum));
            rotText.setFill(Color.WHITE);
            rotText.setFont(Font.font("Arial", FontWeight.BOLD, cr * 1.25));
            StackPane rotBadge = new StackPane(chip, rotText);
            rotBadge.setPrefSize(cr * 2, cr * 2);
            rotBadge.setLayoutX(2 * r - cr);
            rotBadge.setLayoutY(-cr);
            container.getChildren().add(rotBadge);
        }

        boolean showNames = showNamesToggle.isSelected();
        boolean showRoles = showRolesToggle.isSelected();
        if (showNames || showRoles) {
            String text = (showNames && showRoles)
                ? p.getName() + "  " + p.getPositionName()
                : showNames ? p.getName() : p.getPositionName();
            Text infoText = new Text(text);
            infoText.setFill(Color.WHITE);
            infoText.setFont(Font.font("Arial", FontWeight.BOLD, Math.max(r * 0.45, 9)));
            infoText.setEffect(new DropShadow(2, 0, 1, Color.BLACK));
            HBox labelWrap = new HBox(infoText);
            labelWrap.setAlignment(Pos.CENTER);
            labelWrap.setPrefWidth(2 * r + 20);
            labelWrap.setLayoutX(-10);
            labelWrap.setLayoutY(2 * r + 3);
            container.getChildren().add(labelWrap);
        }

        container.setLayoutX(cx - r);
        container.setLayoutY(cy - r);
        addDragHandlers(container, p, isHome, r);
        return container;
    }

    /**
     * Attaches mouse-pressed, dragged, and released handlers to a token container so the user can
     * drag it to reposition the player or click (no drag) to open the edit popup.
     */
    private void addDragHandlers(Pane container, Position pos, boolean isHome, double r) {
        final double[] last = new double[2];
        final boolean[] wasDragged = {false};

        container.setOnMousePressed(e -> {
            last[0] = e.getSceneX();
            last[1] = e.getSceneY();
            wasDragged[0] = false;
            container.toFront();
            e.consume();
        });

        container.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - last[0];
            double dy = e.getSceneY() - last[1];
            last[0] = e.getSceneX();
            last[1] = e.getSceneY();

            double w      = courtPane.getWidth();
            double h      = courtPane.getHeight();
            double pad    = PADDING;
            double half   = w / 2.0;
            double innerH = h - 2 * pad;
            double halfW  = half - pad;

            double lx = Math.clamp(container.getLayoutX() + dx,
                isHome ? pad - r : half - r,
                isHome ? half - r : w - pad - r);
            double ly = Math.clamp(container.getLayoutY() + dy,
                pad - r, h - pad - r);

            container.setLayoutX(lx);
            container.setLayoutY(ly);

            double cx = lx + r;
            double cy = ly + r;

            if (isHome) {
                pos.setYPos(Math.clamp((cx - pad) / halfW,   0.0, 1.0));
                pos.setXPos(Math.clamp((cy - pad) / innerH,  0.0, 1.0));
            } else {
                pos.setYPos(Math.clamp(1.0 - (cx - half) / halfW,  0.0, 1.0));
                pos.setXPos(Math.clamp(1.0 - (cy - pad)  / innerH, 0.0, 1.0));
            }

            wasDragged[0] = true;
            e.consume();
        });

        container.setOnMouseReleased(e -> {
            if (!wasDragged[0]) showEditPanel(pos, e.getScreenX(), e.getScreenY(), isHome);
            e.consume();
        });
    }

    /** Shows a floating popup near the clicked token allowing the user to change the role label or substitute a player. */
    private void showEditPanel(Position pos, double screenX, double screenY, boolean isHome) {
        if (editPopup != null) editPopup.hide();

        TextField roleField = new TextField(pos.getPositionName());
        roleField.setPromptText("Role / position");

        ComboBox<Player> subCombo = new ComboBox<>();
        subCombo.setCellFactory(lv -> playerCell());
        subCombo.setButtonCell(playerCell());
        Team team = isHome ? homeTeam : awayTeam;
        if (team != null) {
            subCombo.setItems(team.getRoster().playerList());
            subCombo.setValue(pos.getPlayer());
        }

        Button applyBtn = new Button("Apply");

        VBox panel = new VBox(6);
        panel.setPadding(new Insets(8));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #888;"
                     + "-fx-border-width: 1;"
                     + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.3),6,0,2,2);");
        panel.getChildren().addAll(new Label("Role:"), roleField, new Label("Sub:"), subCombo, applyBtn);

        editPopup = new Popup();
        editPopup.setAutoHide(true);
        editPopup.getContent().add(panel);

        applyBtn.setOnAction(e -> {
            String role = roleField.getText().trim();
            if (!role.isEmpty()) pos.setPositionName(role);
            Player selected = subCombo.getValue();
            if (selected != null) pos.setPlayer(selected);
            editPopup.hide();
            redraw();
        });

        editPopup.show(courtPane.getScene().getWindow(), screenX + 10, screenY);
    }

    /** Returns a ListCell that displays a player as "#number name". */
    private ListCell<Player> playerCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "#" + item.getNumber() + " " + item.getName());
            }
        };
    }
}
