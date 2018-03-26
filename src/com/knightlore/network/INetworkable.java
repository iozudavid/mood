package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * NOTE: Implementing this interface will NOT automatically make your object
 * known to the NetworkObjectManager. Use the NetworkObject class for that.
 */
public interface INetworkable {
    /**
     * The default size to make a ByteBuffer used to serialize something in bytes.
     */
    int BYTE_BUFFER_DEFAULT_SIZE = 512;
    
    /**
     * Implement this to encapsulate the state of the object in a ByteBuffer.
     * Use this interface's newByteBuffer static method.
     *
     * @return ByteBuffer which encapsulates the state of object
     */
    ByteBuffer serialize();
    
    /**
     * Implement this to apply the state of the object received by a ByteBuffer
     * on the actual object.
     */
    void deserialize(ByteBuffer buffer);
    
    /**
     * Networked objects must have a unique UUID.
     *
     * @return UUID for the given network object
     */
    UUID getObjectId();
    
    
    /**
     * @return a map of all consumer methods you want to be callable over the
     * network, keyed by the methods' names.
     */
    Map<String, Consumer<ByteBuffer>> getNetworkConsumers();
    
}
