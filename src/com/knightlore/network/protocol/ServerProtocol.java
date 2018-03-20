package com.knightlore.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerProtocol {

    // The number of bytes taken up at the start of each packet with metadata.
    public static final int METADATA_LENGTH = 8;

    // The number of bytes taken up after metadata of each packet with objectid.
    public static final int OBJECTID_LENGTH = 16;

    // where actual stats are written
    public static final int MESSAGE_STARTING_POINT = METADATA_LENGTH + OBJECTID_LENGTH;

    // double will have exactly 8 entries in array
    // when serialize it
    public static final int DOUBLE_TO_BYTES_LENGTH = 8;

    private static final Map<Integer, ServerControl> positionAction;
    static {
        positionAction = new HashMap<>();
        positionAction.put(0, ServerControl.XPOS);
        positionAction.put(1, ServerControl.YPOS);
        positionAction.put(2, ServerControl.XDIR);
        positionAction.put(3, ServerControl.YDIR);
        positionAction.put(4, ServerControl.XPLANE);
        positionAction.put(5, ServerControl.YPLANE);
        // just this for now
    }

    // position in the byte array - key map
    // convention of the server
    // to create the packet
    // array of length 2: array[0] = position from
    // array[1] = position to (not included)
    // construct the server data
    private static final Map<Integer[], ServerControl> indexAction;
    static {
        indexAction = new HashMap<>();
        try {
            // mentain 8 entries for each element
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.XPOS) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.XPOS) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.XPOS); // 0, 8
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.YPOS) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.YPOS) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.YPOS); // 8, 16
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.XDIR) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.XDIR) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.XDIR); // 16, 24
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.YDIR) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.YDIR) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.YDIR); // 32, 40
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.XPLANE) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.XPLANE) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.XPLANE); // 40, 48
            indexAction.put(
                    new Integer[] { getPositionByControl(ServerControl.YPLANE) * DOUBLE_TO_BYTES_LENGTH,
                            (getPositionByControl(ServerControl.YPLANE) + 1) * DOUBLE_TO_BYTES_LENGTH },
                    ServerControl.YPLANE); // 48, 56
            // just this for now
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int TOTAL_LENGTH = METADATA_LENGTH + OBJECTID_LENGTH
            + (indexAction.size()) * ServerProtocol.DOUBLE_TO_BYTES_LENGTH;

    // byte value which will be used for disconnected state
    public static final byte DISCONNECTED_BYTE = (byte) 0;

    // for now we will assume that disconnectedState will be shown by a whole 0
    // byte array
    // ie: from ServerProtocol.MESSAGE_STARTING_POINT to
    // ServerProtocol.TOTAL_LENGTH the array will be populated with byte 0
    public static final byte[] disconnectedState;
    static {
        disconnectedState = new byte[ServerProtocol.TOTAL_LENGTH - ServerProtocol.MESSAGE_STARTING_POINT];
        Arrays.fill(disconnectedState, ServerProtocol.DISCONNECTED_BYTE);
    }

    // get the key by passing index in the array
    public static ServerControl getIndexesByPosition(int i) throws IOException {
        if (i > indexAction.size())
            throw new IOException();
        for (Integer[] element : indexAction.keySet()) {
            if (i >= element[0] && i <= element[1])
                return indexAction.get(element);
        }
        return null;
    }

    // get the index by passing the key
    public static Integer[] getIndexesByControl(ServerControl key) throws IOException {
        if (!indexAction.containsValue(key))
            throw new IOException();
        for (Integer[] i : indexAction.keySet()) {
            if (indexAction.get(i) == key)
                return i;
        }
        return null;
    }

    // get the control by passing the position
    public static ServerControl getControlByPosition(int pos) throws IOException {
        if (!positionAction.containsKey(pos))
            throw new IOException();
        return positionAction.get(pos);
    }

    // get the position by passing the control
    public static Integer getPositionByControl(ServerControl key) throws IOException {
        if (!positionAction.containsValue(key))
            throw new IOException();
        for (Integer i : positionAction.keySet()) {
            if (positionAction.get(i) == key)
                return i;
        }
        return null;
    }

    public static Map<Integer[], ServerControl> getIndexActionMap() {
        return indexAction;
    }

    public static Map<Integer[], ServerControl> getPositionActionMap() {
        return indexAction;
    }

    // Returns generic metadata to be placed at the start of each packet.
    public static byte[] getMetadata() {
        ByteBuffer metadata = ByteBuffer.allocate(METADATA_LENGTH);
        metadata.putLong(System.currentTimeMillis());
        return metadata.array();
    }

    public static UUID bytesAsUuid(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long firstLong = byteBuffer.getLong();
        long secondLong = byteBuffer.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] uuidAsBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static byte[] doubleToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double byteArrayToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    // used on client side
    public static boolean isDisconnectedState(byte[] packet) {
        for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH; i++) {
            if (packet[i] != ServerProtocol.disconnectedState[i - ServerProtocol.MESSAGE_STARTING_POINT])
                return false;
        }
        return true;
    }

}
