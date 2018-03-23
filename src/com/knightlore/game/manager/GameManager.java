package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.GameMode;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.ZombieShared;
import com.knightlore.network.NetworkObject;

public abstract class GameManager extends NetworkObject {

    protected long gameOverTick;
    protected long ticksLeft;

    public GameManager(UUID uuid) {
        super(uuid);
    }

    protected static GameState gameState = GameState.LOBBY;
    public static GameMode desiredGameMode = GameMode.FFA;
    public static int numBots = 0;
    public static int numZombies = 0;

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
        long second = (long) (ticksLeft / GameEngine.UPDATES_PER_SECOND);
        long minute = (long) (second / 60);
        return String.format("%02d:%02d", minute % 60, second % 60);
    }

    @Override
    public ByteBuffer serialize() {
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putInt(gameState.ordinal());
        return buf;
    }

}
