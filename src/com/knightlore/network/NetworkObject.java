package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

/**
 * NOTE: While it is not enforceable in this abstract class, all non-abstract
 * classes implementing NetworkObject MUST provide a static method of the
 * following signature:
 * 
 * public static NetworkObject build(UUID uuid);
 * 
 * This method should return a 'blank' instance of the class, with the provided
 * UUID. Use sensible defaults to set any required attributes, as they will
 * likely be overwritten by deserialisation anyway.
 */
public abstract class NetworkObject extends GameObject implements INetworkable {
    private UUID objectUniqueID;
    private NetworkObjectManager networkObjectManager = GameEngine.getSingleton().getNetworkObjectManager();
    protected Vector2D posT1, dirT1, posT2, dirT2;
    protected final double treshold = 0.05D;

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
    public void init() {
        super.init();
        networkObjectManager.registerNetworkObject(this);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        networkObjectManager.removeNetworkObject(this);
    }

    /**
     * Convenience method for implementors. Returns a new ByteBuffer prefixed
     * with this object's UUID, and the name of the method of this object's
     * remote counterpart to pass the ByteBuffer to.
     * 
     * @param remoteMethod:
     *            The name of the method to call on the remote object.
     * @return: An initialised ByteBuffer, ready to be populated.
     */
    protected ByteBuffer newByteBuffer(String remoteMethod) {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(buf, objectUniqueID.toString());
        NetworkUtils.putStringIntoBuf(buf, remoteMethod);
        return buf;
    }

    /**
     * Return the name of the counterpart client class for this type of object.
     */
    public abstract String getClientClassName();

}
