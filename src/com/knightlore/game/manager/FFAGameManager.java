package com.knightlore.game.manager;

import java.util.Iterator;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.BotInput;
import com.knightlore.game.GameMode;
import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.ZombieServer;
import com.knightlore.game.entity.ZombieShared;
import com.knightlore.game.entity.pickup.PistolPickup;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.game.entity.pickup.WeaponPickup;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.game.world.ServerWorld;
import com.knightlore.utils.Vector2D;

/**
 * Contains data and methods to handle the Free For All game mode.
 * 
 * @author James
 *
 */
public class FFAGameManager extends GameManager {
    
    private static final double ROUND_TIME_SECS = 300;
    private Entity winner;
    
    /**
     * Creates a Game Manager with a random UUID.
     */
    public FFAGameManager() {
        super(UUID.randomUUID());
    }
    
    /**
     * Creates a Game Manager with the given UUID.
     * 
     * @param uuid
     *            the UUID of this network object
     */
    public FFAGameManager(UUID uuid) {
        super(uuid);
    }
    
    /**
     * Sets the game state to be PLAYING and respawns all of the players. Also
     * computes the time when the round will end
     */
    @Override
    public void beginGame() {
        gameState = GameState.PLAYING;
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        Iterator<Player> playerIter = playerManager.getPlayerIterator();
        while (playerIter.hasNext()) {
            Player p = playerIter.next();
            Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
            p.respawn(spawnPos);
        }
        
        gameOverTick = GameEngine.ticker.getTime() + (long) (GameEngine.UPDATES_PER_SECOND * ROUND_TIME_SECS);
        
    }
    
    /**
     * @see com.knightlore.game.manager.FFAGameManager#onEntityDeath(ZombieShared)
     */
    @Override
    public void onEntityDeath(ZombieShared victim, Player inflictor) {
        if (desiredGameMode == GameMode.SURVIVAL) {
            inflictor.addScore(1);
        }
        onEntityDeath(victim);
    }
    
    /**
     * Handles a Zombie death. Respawns the <code>victim</code> at a random
     * place in the map.
     */
    @Override
    public void onEntityDeath(ZombieShared victim) {
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        victim.respawn(spawnPos);
    }
    
    /**
     * Handles a Player death when killed by another player. Gives the
     * <code>inflictor</code> 1 point then calls onEntityDeath(victim);
     * 
     * @see com.knightlore.game.manager.FFAGameManager#onEntityDeath(Player)
     */
    @Override
    public void onEntityDeath(Player victim, Player inflictor) {
        inflictor.addScore(1);
        onEntityDeath(victim);
    }
    
    /**
     * Handles a Player death, reduces the player score by 1, drops their
     * current weapon on the floor and then respawns that player.
     */
    @Override
    public void onEntityDeath(Player victim) {
        victim.addScore(-1);
        // drop the WEAPON in their current position
        spawnPickup(victim.getPosition(), victim.getCurrentWeapon().getWeaponType());
        
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        victim.respawn(spawnPos);
    }
    
    private void spawnPickup(Vector2D pos, WeaponType type) {
        WeaponPickup pickup;
        switch (type) {
        case PISTOL:
            pickup = new PistolPickup(pos, null);
            break;
        // If in doubt, spawn a shotgun.
        case SHOTGUN:
        default:
            pickup = new ShotgunPickup(pos, null);
            break;
        }
        pickup.init();
        // nice adding :)
        GameEngine.getSingleton().getWorld().addEntity(pickup);
        System.out.println(type + " Pickup Created");
    }
    
    /**
     * Determines when if game is over, when the timer runs out, the player with
     * the highest score wins.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        // check for winners
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        
        if (GameEngine.ticker.getTime() > gameOverTick && gameState != GameState.FINISHED) {
            gameState = GameState.FINISHED;
            int highScore = Integer.MIN_VALUE;
            Iterator<Player> playerIter = playerManager.getPlayerIterator();
            while (playerIter.hasNext()) {
                Player p = playerIter.next();
                if (p.getScore() > highScore) {
                    winner = p;
                    highScore = p.getScore();
                }
            }
            gameOver();
        }
    }
    
    @Override
    public void onDestroy() {
    }
    
    /**
     * Sets the game state to LOBBY and spawns the bots.
     */
    @Override
    public void startLobby() {
        gameState = GameState.LOBBY;
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        ServerWorld world = (ServerWorld) GameEngine.getSingleton().getWorld();
        if (desiredGameMode == GameMode.SURVIVAL) {
            for (int i = 0; i < numBots; i++) {
                ZombieServer zom = new ZombieServer(world.getMap().getRandomSpawnPoint());
                zom.init();
                world.addEntity(zom);
            }
        } else {
            for (int i = 0; i < GameManager.numBots; i++) {
                Vector2D pos = world.getMap().getRandomSpawnPoint();
                Player botPlayer = new Player(pos, Vector2D.UP, Team.NONE);
                botPlayer.init();
                botPlayer.setInputModule(new BotInput());
                botPlayer.setName("bot" + i);
                playerManager.addPlayer(botPlayer);
            }
        }
    }
    
    /**
     * Sets the game state to FINISHED, prints the winner to the console.
     */
    @Override
    public void gameOver() {
        gameState = GameState.FINISHED;
        System.out.println("GAME OVER");
        System.out.println(winner.getName() + " wins!");
    }
    
    @Override
    public String getClientClassName() {
        return ClientFFAGameManager.class.getName();
    }
    
}
