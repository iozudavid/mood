package com.knightlore.game.manager;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerManager {
    
    private final List<Player> players = new LinkedList<>();

    private ConcurrentLinkedQueue<Player> playersToAdd = new ConcurrentLinkedQueue<Player>();
    private ConcurrentLinkedQueue<Player> playersToRemove = new ConcurrentLinkedQueue<Player>();
    
    public PlayerManager() {
    }

    public void addPlayer(Player pl) {
        playersToAdd.offer(pl);
    }

    public void removePlayer(Player pl) {
        playersToRemove.offer(pl);
    }

    public Iterator<Player> getPlayerIterator() {
        return players.iterator();
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
    
    public void update() {
        while (playersToAdd.peek() != null) {
            players.add(playersToAdd.poll());
        }
        while (playersToRemove.peek() != null) {
            players.remove(playersToRemove.poll());
        }
    }
    
}
