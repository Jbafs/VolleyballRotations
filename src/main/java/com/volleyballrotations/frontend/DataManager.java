package com.volleyballrotations.frontend;

import com.google.gson.*;
import com.volleyballrotations.backend.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles JSON serialization and deserialization of the entire League to/from the user's home directory.
 */
public class DataManager {
    /** Path to the persistent save file ({@code ~/volleyball_rotations.json}). */
    private static final Path SAVE_FILE =
            Path.of(System.getProperty("user.home"), "volleyball_rotations.json");

    // ── Save ─────────────────────────────────────────────────────────────────

    /** Serializes the entire league to the save file, overwriting any previous data. */
    public static void save(League league) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray teamsArr = new JsonArray();
        for (Team team : league.getTeams()) teamsArr.add(serializeTeam(team));
        root.add("teams", teamsArr);

        try (Writer w = Files.newBufferedWriter(SAVE_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, w);
        }
    }

    private static JsonObject serializeTeam(Team team) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",    team.getId());
        obj.addProperty("name",  team.getName());
        obj.addProperty("color", team.getColor());

        JsonArray rosterArr = new JsonArray();
        for (Player p : team.getRoster().playerList()) rosterArr.add(serializePlayer(p));
        obj.add("roster", rosterArr);

        JsonArray lineupsArr = new JsonArray();
        for (Lineup l : team.lineupList()) lineupsArr.add(serializeLineup(l));
        obj.add("lineups", lineupsArr);

        return obj;
    }

    private static JsonObject serializePlayer(Player p) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",         p.getId());
        obj.addProperty("name",       p.getName());
        obj.addProperty("number",     p.getNumber());
        obj.addProperty("defaultPos", p.getPos());
        return obj;
    }

    private static JsonObject serializeLineup(Lineup l) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",   l.getId());
        obj.addProperty("name", l.getName());

        JsonArray rotArr = new JsonArray();
        for (Rotation r : l.rotationList()) rotArr.add(serializeRotation(r));
        obj.add("rotations", rotArr);
        return obj;
    }

    private static JsonObject serializeRotation(Rotation r) {
        JsonObject obj = new JsonObject();
        obj.addProperty("rotation", r.getRotation());

        JsonArray statesArr = new JsonArray();
        for (State s : r.stateList()) statesArr.add(serializeState(s));
        obj.add("states", statesArr);
        return obj;
    }

    private static JsonObject serializeState(State s) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",   s.getId());
        obj.addProperty("name", s.getName());

        JsonArray posArr = new JsonArray();
        for (Position p : s.positionList()) posArr.add(serializePosition(p));
        obj.add("positions", posArr);
        return obj;
    }

    private static JsonObject serializePosition(Position p) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x",            p.getXPos());
        obj.addProperty("y",            p.getYPos());
        obj.addProperty("playerId",     p.getPlayer().getId());
        obj.addProperty("positionName", p.getPositionName());
        return obj;
    }

    // ── Load ─────────────────────────────────────────────────────────────────

    /** Returns null if the save file doesn't exist yet. */
    public static League load() throws IOException {
        if (!Files.exists(SAVE_FILE)) return null;

        try (Reader r = Files.newBufferedReader(SAVE_FILE)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            League league = new League();

            boolean hasDefault = false;
            for (JsonElement el : root.getAsJsonArray("teams")) {
                Team team = deserializeTeam(el.getAsJsonObject());
                if (League.DEFAULT_TEAM_ID.equals(team.getId())) hasDefault = true;
                league.getTeams().add(team);
            }
            if (!hasDefault) league.getTeams().add(0, League.createDefaultTeam());

            return league;
        }
    }

    private static Team deserializeTeam(JsonObject obj) {
        String id    = obj.get("id").getAsString();
        String name  = obj.get("name").getAsString();
        String color = obj.has("color") ? obj.get("color").getAsString() : "#4488ff";

        // Build player map first so positions can reference them
        Roster roster = new Roster();
        Map<String, Player> playerMap = new HashMap<>();
        for (JsonElement el : obj.getAsJsonArray("roster")) {
            Player p = deserializePlayer(el.getAsJsonObject());
            roster.playerList().add(p);
            playerMap.put(p.getId(), p);
        }

        List<Lineup> lineups = new ArrayList<>();
        for (JsonElement el : obj.getAsJsonArray("lineups")) {
            lineups.add(deserializeLineup(el.getAsJsonObject(), playerMap));
        }

        return new Team(id, name, color, roster, lineups.toArray(new Lineup[0]));
    }

    private static Player deserializePlayer(JsonObject obj) {
        return new Player(
                obj.get("id").getAsString(),
                obj.get("name").getAsString(),
                obj.get("number").getAsInt(),
                obj.get("defaultPos").getAsString()
        );
    }

    private static Lineup deserializeLineup(JsonObject obj, Map<String, Player> players) {
        String id   = obj.get("id").getAsString();
        String name = obj.get("name").getAsString();

        JsonArray rotArr = obj.getAsJsonArray("rotations");
        Rotation[] rotations = new Rotation[rotArr.size()];
        for (int i = 0; i < rotArr.size(); i++) {
            rotations[i] = deserializeRotation(rotArr.get(i).getAsJsonObject(), players);
        }

        Lineup lineup = new Lineup(id, rotations);
        lineup.setName(name);
        return lineup;
    }

    private static Rotation deserializeRotation(JsonObject obj, Map<String, Player> players) {
        int rot = obj.get("rotation").getAsInt();
        JsonArray statesArr = obj.getAsJsonArray("states");
        State[] states = new State[statesArr.size()];
        for (int i = 0; i < statesArr.size(); i++) {
            states[i] = deserializeState(statesArr.get(i).getAsJsonObject(), players);
        }
        return new Rotation(rot, states);
    }

    private static State deserializeState(JsonObject obj, Map<String, Player> players) {
        String id   = obj.get("id").getAsString();
        String name = obj.get("name").getAsString();

        JsonArray posArr = obj.getAsJsonArray("positions");
        Position[] positions = new Position[posArr.size()];
        for (int i = 0; i < posArr.size(); i++) {
            positions[i] = deserializePosition(posArr.get(i).getAsJsonObject(), players);
        }
        return new State(id, name, positions);
    }

    private static Position deserializePosition(JsonObject obj, Map<String, Player> players) {
        double x       = obj.get("x").getAsDouble();
        double y       = obj.get("y").getAsDouble();
        String posName = obj.get("positionName").getAsString();
        Player player  = players.get(obj.get("playerId").getAsString());
        return new Position(x, y, player, posName);
    }
}
