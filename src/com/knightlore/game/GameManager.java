package com.knightlore.game;

import java.util.UUID;

import com.knightlore.network.NetworkObject;

public abstract class GameManager extends NetworkObject {

    protected long gameOverTick;
    protected long ticksLeft;

    public GameManager(UUID uuid) {
        super(uuid);
    }

    protected static GameState gameState = GameState.FINISHED;

    public static GameState getGameState() {
        return gameState;
    }
    
    public abstract void startLobby();
    
    public abstract void beginGame();
    
    public abstract void gameOver();
    
    public abstract void onPlayerDeath(Player p);
    
    public abstract void awardScore(Player p, int score);
    
    public String timeLeftString() {
        long second = (ticksLeft / 1000) % 60;
        long minute = (ticksLeft / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minute, second);
    }
    
}
