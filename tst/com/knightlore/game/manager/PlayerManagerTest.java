package com.knightlore.game.manager;

import com.knightlore.game.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PlayerManagerTest {
    
    private PlayerManager manager;
    private Player[] players;
    
    @Before
    public void setUp() {
        // Given
        manager = new PlayerManager();
        players = new Player[4];
        
        for (int i = 0; i < 4; i++) {
            Player player = mock(Player.class);
            manager.addPlayer(player);
            players[i] = player;
        }
        
        manager.update();
    }
    
    @Test
    public void addPlayer_noUpdate() {
        Player player = mock(Player.class);
        
        // When
        manager.addPlayer(player);
        Iterator<Player> playerIterator = manager.getPlayerIterator();
        
        // Then
        assertThat(manager.numPlayer(), is(4));
        for (int i = 0; i < 4; i++) {
            assertThat(playerIterator.hasNext(), is(true));
            playerIterator.next();
        }
        
        assertThat(playerIterator.hasNext(), is(false));
    }
    
    @Test
    public void removePlayer_noUpdate() {
        // When
        for (int i = 0; i < 4; i++) {
            manager.removePlayer(players[i]);
        }
        
        Iterator<Player> playerIterator = manager.getPlayerIterator();
        
        // Then
        assertThat(manager.numPlayer(), is(4));
        for (int i = 0; i < 4; i++) {
            assertThat(playerIterator.hasNext(), is(true));
            playerIterator.next();
        }
        
        assertThat(playerIterator.hasNext(), is(false));
    }
    
    @Test
    public void addPlayer_update() {
        Player player = mock(Player.class);
        
        // When
        manager.addPlayer(player);
        manager.update();
        Iterator<Player> playerIterator = manager.getPlayerIterator();
        
        // Then
        assertThat(manager.numPlayer(), is(5));
        for (int i = 0; i < 5; i++) {
            assertThat(playerIterator.hasNext(), is(true));
            playerIterator.next();
        }
        
        assertThat(playerIterator.hasNext(), is(false));
    }
    
    @Test
    public void removePlayer_update() {
        // When
        for (int i = 0; i < 4; i++) {
            manager.removePlayer(players[i]);
        }
        manager.update();
        
        Iterator<Player> playerIterator = manager.getPlayerIterator();
        
        // Then
        assertThat(manager.numPlayer(), is(0));
        assertThat(playerIterator.hasNext(), is(false));
    }
}