package com.knightlore.game;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.utils.Vector2D;

public class TDMGame extends GameManager {
    
    public TDMGame(UUID uuid) {
        super(uuid);
    }

    private static final double ROUND_TIME_SECS = 300;
    private long nextScoreUpdate;
    private int blueScore;
    private int redScore;
    private static final long SCORE_UPDATE_DELAY = 20;
    
    @Override
    public void startLobby() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void beginGame() {
        gameState = GameState.PLAYING;
        gameOverTick = GameEngine.ticker.getTime() + (long) (GameEngine.UPDATES_PER_SECOND * ROUND_TIME_SECS);
        
    }
    
    @Override
    public void gameOver() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onPlayerDeath(Player p) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void awardScore(Player p, int score) {
        p.addScore(-1);
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        p.respawn(spawnPos);
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onUpdate() {
        
        if (GameEngine.ticker.getTime() > gameOverTick && gameState != GameState.FINISHED) {
            gameState = GameState.FINISHED;
        }
        
        if (GameEngine.ticker.getTime() > nextScoreUpdate) {
            // compute scores
            
            PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
            synchronized (playerManager) {
                blueScore = 0;
                redScore = 0;
                for (Player p : playerManager.getPlayers()) {
                    if (p.team == Team.blue) {
                        blueScore += p.getScore();
                    } else if (p.team == Team.red) {
                        redScore += p.getScore();
                    }
                }
            }
        }
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String timeLeftString() {
        long time = gameOverTick - GameEngine.ticker.getTime();
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public ByteBuffer serialize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getClientClassName() {
        return TDMGameClient.class.getName();
    }
    
}
