package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.knightlore.engine.GameObject;

public abstract class NetworkObjectManager extends GameObject
        implements INetworkable {
    private static NetworkObjectManager singleton;
    // This is a special UUID that refers to the NetworkObjectManager itself.
    public static final UUID MANAGER_UUID = UUID
            .fromString("00000000-0000-0000-0000-000000000000");
    protected Map<String, Consumer<ByteBuffer>> networkConsumers = new HashMap<>();

    protected BlockingQueue<ByteBuffer> messages = new LinkedBlockingQueue<>();

    public NetworkObjectManager() {
        super();
        singleton = this;
    }

    public void processMessage(ByteBuffer buffer) {
        this.messages.add(buffer);
    }

    public abstract void registerNetworkObject(INetworkable obj);

    public abstract void removeNetworkObject(INetworkable obj);

    // public abstract INetworkable getNetworkObject(UUID uuid);

    public static NetworkObjectManager getSingleton() {
        return singleton;
    }

    @Override
    public Map<String, Consumer<ByteBuffer>> getNetworkConsumers() {
        return networkConsumers;
    }

    @Override
    public ByteBuffer serialize() {
        // N.B. The manager should never be serialised...
        return null;
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        // N.B. The manager should never be serialised...
    }

    @Override
    public UUID getObjectId() {
        return MANAGER_UUID;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

}
