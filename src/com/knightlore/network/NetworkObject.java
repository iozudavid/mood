package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.knightlore.engine.GameObject;
import com.knightlore.utils.Vector2D;

public abstract class NetworkObject extends GameObject implements INetworkable {
    private UUID objectUniqueID;

    protected Map<String, Consumer<ByteBuffer>> networkConsumers = new HashMap<>();

    public NetworkObject(UUID uuid) {
        this(uuid, Vector2D.ZERO);

    }

    public NetworkObject(UUID uuid, Vector2D position) {
        super(position);
        this.objectUniqueID = uuid;
        setNetworkConsumers();
    }
    
    private void setNetworkConsumers() {
        networkConsumers.put("deserialize", this::deserialize);
    }

    public synchronized UUID getObjectId() {
        return this.objectUniqueID;
    }

    @Override
    public Map<String, Consumer<ByteBuffer>> getNetworkConsumers() {
        return networkConsumers;
    }
    
    @Override
    public void onCreate() {
        NetworkObjectManager.getSingleton().registerNetworkObject(this);
    }

    @Override
    public void onDestroy() {
        NetworkObjectManager.getSingleton().removeNetworkObject(this);
    }


    // Convenience method for implementors. Returns a new ByteBuffer prefixed
    // with this object's UUID, and the name of the method of this object's
    // remote counterpart to pass the ByteBuffer to.
    protected ByteBuffer newByteBuffer(String remoteMethod) {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_MAX_SIZE);
        buf.put(objectUniqueID.toString().getBytes());
        buf.put(remoteMethod.getBytes());
        return buf;
    }

}
