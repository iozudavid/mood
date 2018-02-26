package com.knightlore.game.entity;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.*;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.world.GameWorld;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.Minimap;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pathfinding.PathFinder;

public class Zombie extends Entity {
    private static final long THINKING_FREQUENCY = 1000; // ms

    private final GameWorld world = GameEngine.getSingleton().getWorld();
    private final PathFinder pathFinder = new PathFinder(world.getMap().getCostGrid());
    private long lastThinkingTime = 0;
    private List<Point> currentPath = new LinkedList<>();

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new Zombie(uuid, 0, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public Zombie(double size, Vector2D position) {
        this(size, position, Vector2D.UP);
    }

    public Zombie(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
        zOffset = 100;
    }

    protected Zombie(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
        zOffset = 100;
    }

    @Override
    public int getDrawSize() {
        return Minimap.SCALE / 2;
    }

    @Override
    public int getMinimapColor() {
        // make it white
        return 0xFFFFFF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.PLAYER_DIRECTIONAL_SPRITE;
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

        Vector2D nextPointDirection = Vector2D.sub(this.position, new Vector2D(currentPath.get(0)));
        if (Vector2D.ZERO.subtract(this.direction).isEqualTo(nextPointDirection)) {
            rotateClockwise();
            return;
        }

        double turnDirection = nextPointDirection.cross(this.direction);
        if (turnDirection > 0) {
            rotateAntiClockwise();
        } else {
            rotateClockwise();
        }

        double directionsDot = nextPointDirection.dot(this.direction);
        if (directionsDot < 0) {
            moveForward();
        }

        if (this.position.toPoint().equals(currentPath.get(0))) {
            currentPath.remove(0);
        }
    }

    private void think() {
        pathFinder.setCostGrid(world.getMap().getCostGrid());
        List<Player> players = world.getPlayerManager().getPlayers();
        Optional<List<Point>> pathToClosestPlayer = players.stream()
                .map(player -> pathFinder.findPath(this.position, player.getPosition()))
                .min(Comparator.comparing(List::size));

        pathToClosestPlayer.ifPresent(points -> currentPath = points);
        lastThinkingTime = System.currentTimeMillis();
    }

    // TODO serialize
}
