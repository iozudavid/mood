package com.knightlore.game;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.utils.Vector2D;

public class FFAGame extends GameManager {
    
    private static final int WIN_SCORE = 10;
    private static final double ROUND_TIME_SECS = 300;
    private Entity winner;
    
    public FFAGame(UUID uuid) {
        super(uuid);
    }
    
    public FFAGame() {
        super(UUID.randomUUID());
    }
    
    @Override
    public void beginGame() {
        gameState = GameState.PLAYING;
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        List<Player> players = playerManager.getPlayers();
        
        for (Player p : players) {
            Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
            p.respawn(spawnPos);
        }
        
        gameOverTick = GameEngine.ticker.getTime() + (long) (GameEngine.UPDATES_PER_SECOND * ROUND_TIME_SECS);
        
    }
    
    @Override
    public void onPlayerDeath(Player p) {
        // reduce their score for dying
        p.addScore(-1);
        // drop the WEAPON in their current position
        
        spawnPickup(p.getPosition(), p.getCurrentWeapon().getWeaponType());
        
        // generate random SPAWN point
        Vector2D spawnPos = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        p.respawn(spawnPos);
    }
    
    private void spawnPickup(Vector2D pos, WeaponType type) {
        switch (type) {
        case PISTOL:
            break;
        case SHOTGUN:
            ShotgunPickup shotgun = new ShotgunPickup(pos, null);
            shotgun.init();
            // nice adding :)
            GameEngine.getSingleton().getWorld().addEntity(shotgun);
            System.out.println("Shotgun Pickup Created");
            break;
        }
        
    }
    
    @Override
    public void onCreate() {
    }
    
    @Override
    public void onUpdate() {
        // update ticks left
        if (gameState != GameState.FINISHED) {
            ticksLeft = gameOverTick - GameEngine.ticker.getTime();
        }
        
        // check for winners
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        List<Player> players = playerManager.getPlayers();
        
        if (GameEngine.ticker.getTime() > gameOverTick && gameState != GameState.FINISHED) {
            gameState = GameState.FINISHED;
            int highScore = Integer.MIN_VALUE;
            for (Player p : players) {
                if (p.getScore() > highScore) {
                    winner = p;
                    highScore = p.getScore();
                }
            }
            gameOver();
            return;
        }
        
        for (Player p : players) {
            if (p.getScore() >= WIN_SCORE) {
                winner = p;
                gameOver();
                return;
            }
        }
    }
    
    @Override
    public void onDestroy() {
    }
    
    @Override
    public void awardScore(Player p, int score) {
        p.addScore(score);
    }
    
    @Override
    public void startLobby() {
        gameState = GameState.LOBBY;
    }
    
    @Override
    public void gameOver() {
        gameState = GameState.FINISHED;
        System.out.println("GAME OVER");
        System.out.println(winner.getName() + " wins!");
    }
    
    @Override
    public ByteBuffer serialize() {
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putLong(ticksLeft);
        return buf;
    }
    
    @Override
    public void deserialize(ByteBuffer buffer) {
        ticksLeft = buffer.getLong();
    }
    
    @Override
    public String getClientClassName() {
        return ClientFFAGame.class.getName();
    }
    
}
