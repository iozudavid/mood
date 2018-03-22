package com.knightlore.game.entity;

import com.knightlore.GameSettings;
import com.knightlore.game.manager.AIManager;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.world.GameWorld;
import com.knightlore.utils.Vector2D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ZombieServer extends ZombieShared {
    private static final double DIRECTION_DIFFERENCE_TO_TURN = 0.1d;
    private static final long THINKING_FREQUENCY = 1000; // ms
    private static final int MAX_HEALTH = 100;
    private static final int DAMAGE = 20;
    private static final double ATTACK_RANGE = 1D;
    
    private final GameWorld world = GameEngine.getSingleton().getWorld();
    private long lastThinkingTime = 0;
    private List<Point> currentPath = new LinkedList<>();
    private int currentHealth = MAX_HEALTH;
    private Entity lastInflictor;
    
    public ZombieServer(Vector2D position) {
        super(position);
        this.rotationSpeed = 0.1D;
    }
    
    @Override
    public void onUpdate() {
        checkDeath();
        
        if (System.currentTimeMillis() - lastThinkingTime > THINKING_FREQUENCY) {
            think();
        }
        move();
    }
    
    private void checkDeath() {
        if(GameSettings.isClient()) {
            return;
        }
        if (currentHealth <= 0) {
            if(lastInflictor == null) {
                System.out.println(this.getName() + " was killed by natural causes");
            }else {
                System.out.println(this.getName() + " was killed by " + lastInflictor.getName());
                lastInflictor.killConfirmed(this);
            }            
            removeAllBuffs();
            this.sendSystemMessage(this.getName(), lastInflictor);
            this.destroy();
        }
    }
    
    private void think() {
        AIManager aiManager = world.getAiManager();
        List<Player> players = world.getPlayerManager().getPlayers();
        List<List<Point>> pathsToPlayers = players.stream()
                .map(player -> aiManager.findRawPath(this.position, player.getPosition()))
                .collect(Collectors.toList());
        
        if (pathsToPlayers.isEmpty()) {
            return;
        }
        
        Entity closestPlayer = players.get(0);
        List<Point> shortestPath = pathsToPlayers.get(0);
        for (int i = 0; i < players.size(); i++) {
            List<Point> path = pathsToPlayers.get(i);
            if (path.size() < shortestPath.size()) {
                closestPlayer = players.get(i);
                shortestPath = path;
            }
        }
        
        currentPath = aiManager.pruneUnnecessaryNodes(shortestPath);
        double distance = closestPlayer.getPosition().distance(this.position);
        if (distance <= ATTACK_RANGE) {
            closestPlayer.takeDamage(DAMAGE, this);
            System.out.println("Zombie attacked " + closestPlayer.getName());
        }
        
        lastThinkingTime = System.currentTimeMillis();
    }
    
    private void move() {
        if (this.currentPath.isEmpty()) {
            return;
        }
        
        Vector2D nextPointDirection = Vector2D.sub(new Vector2D(currentPath.get(0)), this.position);
        if (Vector2D.ZERO.subtract(this.direction).equals(nextPointDirection)) {
            rotateClockwise();
            return;
        }
        
        double turnDirection = nextPointDirection.cross(this.direction);
        if (turnDirection < -DIRECTION_DIFFERENCE_TO_TURN) {
            rotateAntiClockwise();
        } else if (turnDirection > DIRECTION_DIFFERENCE_TO_TURN) {
            rotateClockwise();
        } else {
            moveForward();
        }
        
        if (this.position.toPoint().equals(currentPath.get(0))) {
            currentPath.remove(0);
        }
    }
    
    @Override
    public void onCollide(Player player) {
    }
    
    @Override
    public void takeDamage(int damage, Entity inflictor) {
        currentHealth -= damage;
        if(inflictor != null) {
            lastInflictor = inflictor;
        }
    }

}
