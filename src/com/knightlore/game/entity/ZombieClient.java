package com.knightlore.game.entity;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.utils.Vector2D;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ZombieClient extends ZombieShared {

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
    }

    @Override
    public void onCollide(Player player) {
    }

}
