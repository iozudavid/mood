package com.knightlore.network.protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Protocol of how data is encapsulating in a packet by the client
 *
 * @author David Iozu, Will Miller
 */
public final class ClientProtocol {
    
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
     * @return the index - control map convention
     */
    public static Map<Integer, ClientController> getIndexActionMap() {
        return indexAction;
    }
}
