package com.knightlore.game;

import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class FFAGame extends GameManager {
    
    private static final int WIN_SCORE = 10;
    private Entity winner;

    @Override
    public void beginGame() {
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        List<Player> players = playerManager.getPlayers();
        
        
        for(Player p : players) {
            Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
            p.respawn(spawnPos);
        }
    }
    
    @Override
    public void onPlayerDeath(Player p) {
        p.decreaseScore(1);
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        p.respawn(spawnPos);
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        List<Player> players = playerManager.getPlayers();
        for(Player p : players) {
            if(p.getScore() >= WIN_SCORE) {
                winner = p;
                gameOver();
                return;
            }
        }
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void awardScore(Player p, int score) {
        p.increaseScore(score);
    }

    @Override
    public void startLobby() {
        gameState = GameState.lobby;
        
    }

    @Override
    public void gameOver() {
        gameState = GameState.finished;
        System.out.println("GAME OVER");
        System.out.println(winner.getName() + " wins!");
    }
}
