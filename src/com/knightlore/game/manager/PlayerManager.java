package com.knightlore.game.manager;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Player;

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
    
    public int numPlayer() {
        return players.size();
    }
    
    public int numPlayers(Team t) {
        int num = 0;
        for(Player p : players) {
            if(p.team == t) {
                num++;
            }
        }
        return num;
    }
    
}
