package com.knightlore.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//this class contains the protocol
//of how data is encapsulating in a packet
//by the client
public final class ClientProtocol {
    // The number of bytes taken up at the start of each packet with metadata.
    public static final int METADATA_LENGTH = 8;

    // position in the byte array - key map
    // convention of the client
    // to create the packet
    private static final Map<Integer, ClientControl> indexAction;
    static {
        indexAction = new HashMap<Integer, ClientControl>();
        indexAction.put(0, ClientControl.FORWARD);
        indexAction.put(1, ClientControl.ROTATE_ANTI_CLOCKWISE);
        indexAction.put(2, ClientControl.BACKWARD);
        indexAction.put(3, ClientControl.ROTATE_CLOCKWISE);
        // just this for now
    }

    // get the key by passing index in the array
    public static ClientControl getByIndex(int i) throws IOException {
        if (!indexAction.containsKey(i))
            throw new IOException();
        return indexAction.get(i);
    }

    // get the index by passing the key
    public static int getByKey(ClientControl key) throws IOException {
        if (!indexAction.containsValue(key))
            throw new IOException();
        for (int i : indexAction.keySet()) {
            if (indexAction.get(i) == key)
                return i;
        }
        return -1;
    }

    public static Map<Integer, ClientControl> getIndexActionMap() {
        return indexAction;
    }

    // Returns generic metadata to be placed at the start of each packet.
    public static byte[] getMetadata() {
        ByteBuffer metadata = ByteBuffer.allocate(METADATA_LENGTH);
        metadata.putLong(System.currentTimeMillis());
        return metadata.array();
    }

}
