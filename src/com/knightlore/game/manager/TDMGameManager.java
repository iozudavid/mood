package com.knightlore.game.manager;

import java.util.Iterator;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.BotInput;
import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.ZombieShared;
import com.knightlore.game.entity.pickup.PistolPickup;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.game.entity.pickup.WeaponPickup;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.game.world.ServerWorld;
import com.knightlore.utils.Vector2D;

/**
 * Contains data and methods to handle the Team Deathmatch game mode
 * client-side.
 *
 * @author James, Tom
 */
public class TDMGameManager extends GameManager {
    
    private static final double ROUND_TIME_SECS = 300;
    private final long SCORE_UPDATE_PERIOD = (long)GameEngine.UPDATES_PER_SECOND * 5;
    private long nextScoreUpdate;
    private int blueScore = 0;
    private int redScore = 0;
    
    /**
     * Creates a Game Manager with a random UUID.
     */
    public TDMGameManager() {
        super(UUID.randomUUID());
    }
    /**
     * Creates a Game Manager with the given UUID.
     *
     * @param uuid the UUID of this network object
     */
    public TDMGameManager(UUID uuid) {
        super(uuid);
    }
    
    /**
     * Sets the game state to LOBBY and spawns the bots.
     */
    @Override
    public void startLobby() {
        gameState = GameState.LOBBY;
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        ServerWorld world = (ServerWorld)GameEngine.getSingleton().getWorld();
        for (int i = 0; i < GameManager.numBots; i++) {
            // attempt even teams
            Team team = Team.BLUE;
            if (i % 2 == 0) {
                team = Team.RED;
            }
            Vector2D pos = world.getMap().getRandomSpawnPoint(team);
            Player botPlayer = new Player(pos, Vector2D.UP, team);
            botPlayer.init();
            
            botPlayer.setInputModule(new BotInput());
            botPlayer.setName("bot" + i);
            playerManager.addPlayer(botPlayer);
        }
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
            Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint(p.team);
            p.respawn(spawnPos);
        }
        gameOverTick = GameEngine.ticker.getTime() + (long)(GameEngine.UPDATES_PER_SECOND * ROUND_TIME_SECS);
        nextScoreUpdate = SCORE_UPDATE_PERIOD;
    }
    
    /**
     * Ends the game, prints the winning team to the console.
     */
    @Override
    public void gameOver() {
        gameState = GameState.FINISHED;
        System.out.println("GAME OVER");
        String winStr;
        if (blueScore > redScore) {
            winStr = "KNIGHTLORE WINS!";
        } else if (blueScore < redScore) {
            winStr = "THE ANARCHISTS WIN!";
        } else {
            winStr = "DRAW! THE DAY BELONGS TO THE ZOMBIES!";
        }
        
        System.out.println(winStr);
        // grab any entity
        Entity randEnt = GameEngine.getSingleton().getWorld().getEntityIterator().next();
        randEnt.sendSystemMessage(winStr);
    }
    
    /**
     * @see com.knightlore.game.manager.TDMGameManager#onEntityDeath(ZombieShared)
     */
    @Override
    public void onEntityDeath(ZombieShared victim, Player inflictor) {
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
     * <code>inflictor</code> 1 point then calls onEntityDeath(victim).
     * Penalises the <code>inflictor</code> instead if this is a team kill.
     *
     * @see com.knightlore.game.manager.TDMGameManager#onEntityDeath(Player)
     */
    @Override
    public void onEntityDeath(Player victim, Player inflictor) {
        if (victim.team == inflictor.team) {
            // penalise inflictor for TK
            inflictor.addScore(-1);
            // offset death penalty
            victim.addScore(1);
        } else {
            inflictor.addScore(1);
        }
        onEntityDeath(victim);
    }
    
    /**
     * Handles a Player death, reduces the player score by 1, drops their
     * current weapon on the floor and then respawns that player.
     */
    @Override
    public void onEntityDeath(Player victim) {
        victim.addScore(-1);
        spawnPickup(victim.getPosition(), victim.getCurrentWeapon().getWeaponType());
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint(victim.team);
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
     * Determines when the game is over. Computes the team scores.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (GameEngine.ticker.getTime() > gameOverTick && gameState != GameState.FINISHED) {
            gameState = GameState.FINISHED;
            gameOver();
        }
        
        if (GameEngine.ticker.getTime() > nextScoreUpdate) {
            // compute scores
            
            PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
            synchronized (playerManager) {
                blueScore = 0;
                redScore = 0;
                Iterator<Player> playerIter = playerManager.getPlayerIterator();
                while (playerIter.hasNext()) {
                    Player p = playerIter.next();
                    if (p.team == Team.BLUE) {
                        blueScore += p.getScore();
                    } else if (p.team == Team.RED) {
                        redScore += p.getScore();
                    }
                }
            }
            nextScoreUpdate += SCORE_UPDATE_PERIOD;
        }
    }
    
    @Override
    public void onDestroy() {
    }
    
    @Override
    public String getClientClassName() {
        return TDMGameManagerClient.class.getName();
    }
    
}
