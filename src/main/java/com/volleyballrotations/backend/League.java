package com.volleyballrotations.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

/**
 * Top-level container that holds all teams available in the application.
 */
public class League {
    /** Well-known ID reserved for the built-in placeholder team so it survives save/load cycles. */
    public static final String DEFAULT_TEAM_ID = "00000000-0000-0000-0000-000000000000";

    private final ObservableList<Team> teams = FXCollections.observableArrayList();

    public ObservableList<Team> getTeams() { return teams; }

    /** Creates a League pre-populated with a single default placeholder team. */
    public static League createWithDefaults() {
        League l = new League();
        l.teams.add(createDefaultTeam());
        return l;
    }

    /** Builds the default placeholder team with six generic players and one lineup. */
    public static Team createDefaultTeam() {
        Player[] players = new Player[6];
        for (int i = 0; i < 6; i++) {
            players[i] = new Player(UUID.randomUUID().toString(), "Player " + (i + 1), i + 1, "");
        }
        Roster roster = new Roster(players);
        Lineup lineup = new Lineup(players);
        return new Team(DEFAULT_TEAM_ID, "Default", "#888888", roster, lineup);
    }
}
