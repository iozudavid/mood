package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.ZombieShared;
import com.knightlore.network.NetworkObject;

/**
 * Representation of a Game Mode in the game. Stores important gameplay
 * information, specifically the time left and the current state of the game.
 * <p>
 * Note: This object only needs to be created server side, as it will be
 * automatically replicated on the client side.
 * 
 * @author James
 *
 */
public abstract class GameManager extends NetworkObject {
    
    protected long gameOverTick;
    protected long ticksLeft;
    
    public GameManager(UUID uuid) {
        super(uuid);
    }
    
    protected static GameState gameState = GameState.LOBBY;
    
    public static GameState getGameState() {
        return gameState;
    }
    
    /**
     * Called to start the lobby (pre-game).
     */
    public abstract void startLobby();
    
    /**
     * Called to start the game.
     */
    public abstract void beginGame();
    
    /**
     * Called to end the game.
     */
    public abstract void gameOver();
    
    /**
     * Handles a Zombie death, when caused by a Player.
     * 
     * @param victim
     *            the Zombie who
     * @param inflictor
     *            the Player who caused the kill
     */
    public abstract void onEntityDeath(ZombieShared victim, Player inflictor);
    
    /**
     * Handles a Zombie death.
     * 
     * @param victim
     *            the Zombie that died
     */
    public abstract void onEntityDeath(ZombieShared victim);
    
    /**
     * Handles a Player death, when caused by another Player.
     * 
     * @param victim
     *            the Player who died
     * @param inflictor
     *            the Player who caused the kill
     */
    public abstract void onEntityDeath(Player victim, Player inflictor);
    
    /**
     * Handles a Player death.
     * 
     * @param victim
     *            the Player who was killed
     */
    public abstract void onEntityDeath(Player victim);
    
    /**
     * Called by the GameEngine. Computes the time left server side.
     */
    @Override
    public void onUpdate() {
        if (GameSettings.isClient()) {
            return;
        }
        // update ticks left
        if (gameState != GameState.FINISHED) {
            ticksLeft = gameOverTick - GameEngine.ticker.getTime();
        }
    }
    
    /**
     * @returns A Minutes:Seconds representation of the current time remaining.
     *          e.g. 03:27
     */
    public String timeLeftString() {
        long second = (long) (ticksLeft / GameEngine.UPDATES_PER_SECOND);
        long minute = (long) (second / 60);
        return String.format("%02d:%02d", minute % 60, second % 60);
    }
    
    /**
     * serialises the state of this object into a ByteBuffer
     * 
     * @see deserialize()
     */
    @Override
    public ByteBuffer serialize() {
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putInt(gameState.ordinal());
        buf.putLong(ticksLeft);
        return buf;
    }
    
    /**
     * Deserialises the state of this object from a ByteBuffer
     * 
     * @see serialise()
     */
    @Override
    public void deserialize(ByteBuffer buffer) {
        gameState = GameState.values()[buffer.getInt()];
        ticksLeft = buffer.getLong();
    }
    
}
