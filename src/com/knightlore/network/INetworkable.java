package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

// NOTE: Implementing this interface will NOT automatically make your object known to the NetworkObjectManager. 
// Use the NetworkObject class for that.
public interface INetworkable {
    // The default size to make a ByteBuffer used to serialise something in
    // bytes.
    public static final int BYTE_BUFFER_DEFAULT_SIZE = 512;

    // Implement this to encapsulate the state of the object in a ByteBuffer.
    // Use this interface's newByteBuffer static method.
    public ByteBuffer serialize();

    // This should be the inverse function of serialize.
    public void deserialize(ByteBuffer buffer);

    // Networked objects must have a unique UUID.
    public UUID getObjectId();

    // Must return a map of all consumer methods you want to be callable over
    // the network, keyed by the methods' names.
    Map<String, Consumer<ByteBuffer>> getNetworkConsumers();
}
