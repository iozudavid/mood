package com.knightlore.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Protocol of how data is encapsulating in a packet by the client
 *
 * @author David Iozu, Will Miller
 */
public final class ClientProtocol {
    /**
     * The number of bytes taken up at the start of each packet with metadata.
     */
    public static final int METADATA_LENGTH = 8;

    /**
     * Position in the byte array - key map convention of the client to create the packet
     */
    private static final Map<Integer, ClientController> indexAction;

    static {
        indexAction = new HashMap<>();
        indexAction.put(0, ClientController.FORWARD);
        indexAction.put(1, ClientController.LEFT);
        indexAction.put(2, ClientController.BACKWARD);
        indexAction.put(3, ClientController.RIGHT);
        indexAction.put(4, ClientController.ROTATE_ANTI_CLOCKWISE);
        indexAction.put(5, ClientController.ROTATE_CLOCKWISE);
        indexAction.put(6, ClientController.SHOOT);
    }

    /**
     * Pass the index of the control and retrieve the actual control.
     *
     * @param i - index in key map
     * @return actual control found at the given index
     * @throws IOException when index not found in map
     */
    public static ClientController getByIndex(int i) throws IOException {
        if (!indexAction.containsKey(i)) {
            throw new IOException();
        }
        return indexAction.get(i);
    }

    /**
     * Pass the control and receive its index in map.
     *
     * @param key - control passed to get the index
     * @return index of the given control
     * @throws IOException when no control is found in map
     */
    public static int getByKey(ClientController key) throws IOException {
        if (!indexAction.containsValue(key)) {
            throw new IOException();
        }
        for (int i : indexAction.keySet()) {
            if (indexAction.get(i) == key) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return the index - control map convention
     */
    public static Map<Integer, ClientController> getIndexActionMap() {
        return indexAction;
    }


    /**
     * @return generic metadata to be placed at the start of each packet.
     */
    public static byte[] getMetadata() {
        ByteBuffer metadata = ByteBuffer.allocate(METADATA_LENGTH);
        metadata.putLong(System.currentTimeMillis());
        return metadata.array();
    }

}
