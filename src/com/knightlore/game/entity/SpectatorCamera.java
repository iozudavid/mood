package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class SpectatorCamera extends Entity {

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new SpectatorCamera(uuid, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public static final double CAMERA_SIZE = 0.25D;

    private double theta = 0;
    private double sweep = 10;

    public SpectatorCamera(Vector2D position, Vector2D direction) {
        this(UUID.randomUUID(), position, direction);
    }

    public SpectatorCamera(UUID uuid, Vector2D position, Vector2D direction) {
        super(uuid, CAMERA_SIZE, position, direction);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        theta = Math.sin(GameEngine.ticker.getTime()) * sweep;

        double xDir = Math.cos(theta), yDir = Math.sin(theta);
        direction = new Vector2D(xDir, yDir);
    }

    @Override
    public int getMinimapColor() {
        return 0x00FF00;
    }

    @Override
    public void onCollide(Player player) {
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.CAMERA_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        return this.getClass().getName();
    }

}
