package com.knightlore.game;

import com.knightlore.engine.GameObject;

public abstract class GameManager extends GameObject {

    protected static GameState gameState = GameState.finished;

    public static GameState getGameState() {
        return gameState;
    }
    
    public abstract void startLobby();
    
    public abstract void beginGame();
    
    public abstract void gameOver();
    
    public abstract void onPlayerDeath(Player p);
    
    public abstract void awardScore(Player p, int score);
    
}
