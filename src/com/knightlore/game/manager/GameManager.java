package com.knightlore.game.manager;

import java.util.Optional;
import java.util.UUID;

import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.ZombieShared;
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
    
    public abstract void onEntityDeath(ZombieShared victim, Player inflictor);
    
    public abstract void onEntityDeath(ZombieShared victim);
    
    public abstract void onEntityDeath(Player victim, Player inflictor);
    
    public abstract void onEntityDeath(Player victim);
    
    public String timeLeftString() {
        long second = (ticksLeft / 1000) % 60;
        long minute = (ticksLeft / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minute, second);
    }
    
}
