package com.knightlore.game.entity;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.world.GameWorld;
import com.knightlore.utils.Vector2D;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ZombieServer extends ZombieShared {
    private static final double DIRECTION_DIFFERENCE_TO_TURN = 0.1d;
    private static final long THINKING_FREQUENCY = 1000; // ms
    private static final int MAX_HEALTH = 10;

    private final GameWorld world = GameEngine.getSingleton().getWorld();
    private long lastThinkingTime = 0;
    private List<Point> currentPath = new LinkedList<>();
    private int currentHealth = MAX_HEALTH;

    public ZombieServer(Vector2D position) {
        super(position);
    }

    protected ZombieServer(Vector2D position, Vector2D direction) {
        super(position, direction);
    }

    protected ZombieServer(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
    }


    @Override
    public void onUpdate() {
        if (System.currentTimeMillis() - lastThinkingTime > THINKING_FREQUENCY) {
            think();
        }

        move();
    }

    private void move() {
        if (this.currentPath.isEmpty()) {
            return;
        }

        Vector2D nextPointDirection = Vector2D.sub( new Vector2D(currentPath.get(0)), this.position);
        if (Vector2D.ZERO.subtract(this.direction).isEqualTo(nextPointDirection)) {
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

    private void think() {
        List<Player> players = world.getPlayerManager().getPlayers();
        Optional<List<Point>> pathToClosestPlayer = players.stream()
                .map(player -> world.getAiManager().findPath(this.position, player.getPosition()))
                .min(Comparator.comparing(List::size));

        pathToClosestPlayer.ifPresent(points -> currentPath = points);
        lastThinkingTime = System.currentTimeMillis();
    }

    @Override
    public void onCollide(Player player) {
    }
    
    @Override
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if(currentHealth <=0) {
            this.position = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
            currentHealth = MAX_HEALTH;
        }
    }
}
