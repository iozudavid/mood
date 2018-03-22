package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.animation.PlayerMoveAnimation;
import com.knightlore.render.animation.PlayerStandAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.ZombieGraphicMatrix;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class ZombieClient extends ZombieShared {

    private PlayerMoveAnimation moveAnim = new PlayerMoveAnimation(
            ZombieGraphicMatrix.getGraphic(ZombieGraphicMatrix.Stance.MOVE));

    private PlayerStandAnimation standAnim = new PlayerStandAnimation(
            ZombieGraphicMatrix.getGraphic(ZombieGraphicMatrix.Stance.STAND),
            (long) (GameEngine.UPDATES_PER_SECOND / 10));

    private Animation<DirectionalSprite> currentAnim = standAnim;

    private Vector2D prevPos;

    public ZombieClient(Vector2D position, Vector2D direction) {
        super(position, direction);
    }

    private ZombieClient(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
    }

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        NetworkObject obj = new ZombieClient(uuid, 0, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    @Override
    public void onUpdate() {
        if (prevPos != null) {
            // The difference between our previous and new positions.
            Vector2D displacement = position.subtract(prevPos);
            double dis = displacement.magnitude();
            moveAnim.update(dis);
        }

        currentAnim = moveAnim.expired() ? standAnim : moveAnim;
        prevPos = position;
    }

    @Override
    public Graphic getGraphic(Vector2D playerPos) {
        return currentAnim.getFrame().getCurrentGraphic(position, direction, playerPos);
    }

    @Override
    public void onCollide(Player player) {
    }

}
