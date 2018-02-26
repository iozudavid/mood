package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.knightlore.engine.GameWorld;
import com.knightlore.network.protocol.NetworkUtils;

public abstract class NetworkObjectManager implements INetworkable, Runnable {

    private static NetworkObjectManager singleton;
    // This is a special UUID that refers to the NetworkObjectManager itself.
    public static final UUID MANAGER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    protected Map<String, Consumer<ByteBuffer>> networkConsumers = new HashMap<>();

    protected BlockingQueue<ByteBuffer> messages = new LinkedBlockingQueue<>();

    protected GameWorld world;

    public NetworkObjectManager(GameWorld world) {
        this.world = world;
        singleton = this;
    }

    public void processMessage(ByteBuffer buffer) {
        this.messages.add(buffer);
    }

    public abstract void registerNetworkObject(NetworkObject obj);

    public abstract void removeNetworkObject(NetworkObject obj);

    public abstract NetworkObject getNetworkObject(UUID uuid);

    public static NetworkObjectManager getSingleton() {
        return singleton;
    }

    @Override
    public Map<String, Consumer<ByteBuffer>> getNetworkConsumers() {
        return networkConsumers;
    }

    @Override
    public synchronized ByteBuffer serialize() {
        // N.B. The manager should never be serialised...
        return null;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buffer) {
        // N.B. The manager should never be serialised...
    }

    @Override
    public UUID getObjectId() {
        return MANAGER_UUID;
    }

    public void init() {
        System.out.println("init called");
        new Thread(this).start();
        System.out.println("Object manager started");
    }

    @Override
    public void run() {
        while (true) {
            ByteBuffer buf = null;
            try {
                buf = this.messages.take();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for a message.");
                e.printStackTrace();
                return;
            }
            UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
            String methodName = NetworkUtils.getStringFromBuf(buf);
            Consumer<ByteBuffer> cons;
            if (objID.equals(MANAGER_UUID))
                // This message is directed at the NetworkManager, i.e. ourself.
                cons = this.getNetworkConsumers().get(methodName);
            else {
                NetworkObject obj = this.getNetworkObject(objID);
                cons = obj.getNetworkConsumers().get(methodName);
            }
            // Execute the specified method on the specified object, with the
            // rest of the ByteBuffer as input.
            cons.accept(buf);
        }
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

}
