package com.knightlore.game.entity;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Zombie extends Entity {

    private static final double DIRECTION_DIFFERENCE_TO_TURN = 0.1d;
    private static final long THINKING_FREQUENCY = 1000; // ms

    private long lastThinkingTime = 0;
    private List<Point> currentPath = new LinkedList<>();

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new Zombie(uuid, 0, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public Zombie(Vector2D position) {
        this(position, Vector2D.UP);
    }

    public Zombie(Vector2D position, Vector2D direction) {
        super(0.25D, position, direction);
        zOffset = 100;
    }

    private Zombie(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
        zOffset = 100;
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

    @Override
    public void onCollide(Player player) {
    }

    private void move() {
        if (this.currentPath.isEmpty()) {
            return;
        }

        Vector2D nextPointDirection = Vector2D.sub(new Vector2D(currentPath.get(0)), this.position);
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
        // List<Player> players = world.getPlayerManager().getPlayers();
        // Optional<List<Point>> pathToClosestPlayer = players.stream()
        // .map(player -> world.getAIManager().findPath(this.position,
        // player.getPosition()))
        // .min(Comparator.comparing(List::size));
        //
        // pathToClosestPlayer.ifPresent(points -> currentPath = points);
        // lastThinkingTime = System.currentTimeMillis();
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }

    // TODO serialize
}
