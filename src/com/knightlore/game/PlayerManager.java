package com.knightlore.game;

import java.util.LinkedList;
import java.util.List;

public class PlayerManager {
    private final List<Player> players = new LinkedList<>();

    public PlayerManager() {
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public List<Player> getPlayers() {
        return players;
    }
}
