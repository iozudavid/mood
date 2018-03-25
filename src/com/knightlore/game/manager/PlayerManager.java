package com.knightlore.game.manager;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A container for all the Player objects that exist in the game.
 * 
 * @author James
 *
 */
public class PlayerManager {
    
    private final List<Player> players = new LinkedList<>();
    
    private ConcurrentLinkedQueue<Player> playersToAdd = new ConcurrentLinkedQueue<Player>();
    private ConcurrentLinkedQueue<Player> playersToRemove = new ConcurrentLinkedQueue<Player>();
    
    public PlayerManager() {
    }
    
    /**
     * Queues a new Player to be added to the manager, will actually be added
     * next world update.
     * 
     * @param pl
     *            the Player to add
     */
    public void addPlayer(Player pl) {
        playersToAdd.offer(pl);
    }
    
    /**
     * Queues a new Player to be removed from the manager, will actually be
     * added next world update.
     * 
     * @param pl
     *            the Player to remove
     */
    public void removePlayer(Player pl) {
        playersToRemove.offer(pl);
    }
    
    public Iterator<Player> getPlayerIterator() {
        return players.iterator();
    }
    
    /**
     * 
     * @returns How many players there are in total
     */
    public int numPlayer() {
        return players.size();
    }
    
    /**
     * 
     * @param t
     *            The Team to search for
     * @returns How many players exist on that team
     */
    public int numPlayers(Team t) {
        int num = 0;
        for (Player p : players) {
            if (p.team == t) {
                num++;
            }
        }
        return num;
    }
    
    /**
     * Called from GameWorld, makes any required changes to the player list.
     */
    public void update() {
        while (playersToAdd.peek() != null) {
            players.add(playersToAdd.poll());
        }
        while (playersToRemove.peek() != null) {
            players.remove(playersToRemove.poll());
        }
    }
    
}
