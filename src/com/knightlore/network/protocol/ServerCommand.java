package com.knightlore.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerCommand {

    private long timeSent;
    private UUID objectId;
    private Map<ServerControl, Double> objectStats;

    public ServerCommand(long timeSent, UUID objectId, Map<ServerControl, Double> objectStats) {
        this.timeSent = timeSent;
        this.objectId = objectId;
        this.objectStats = objectStats;
    }

    public long getTimeSent() {
        return this.timeSent;
    }

    public UUID getObjectId() {
        return this.objectId;
    }

    // Returns null if no value exists for this key.
    public Double getValueByControl(ServerControl aControl) {
        return this.objectStats.get(aControl);
    }

    public static ServerCommand decodePacket(byte[] packet) {
        try {
            // creating the initial map
            Map<ServerControl, Double> objectStats = new HashMap<ServerControl, Double>();

            // Metadata processing.
            ByteBuffer buf = ByteBuffer.wrap(packet);
            long timeSent = buf.getLong();

            // set the new position in order to read objectid
            buf.position(ServerProtocol.METADATA_LENGTH);
            byte[] objectIdByte = new byte[ServerProtocol.OBJECTID_LENGTH];
            buf.get(objectIdByte, 0, ServerProtocol.OBJECTID_LENGTH);

            UUID objectID = ServerProtocol.bytesAsUuid(objectIdByte);

            buf.position(ServerProtocol.MESSAGE_STARTING_POINT);

            int position = 0;
            while (buf.hasRemaining()) {
                ServerControl currentControl = ServerProtocol.getControlByPosition(position);
                double valueOfCurrentControl = buf.getDouble(buf.position());
                objectStats.put(currentControl, valueOfCurrentControl);
                buf.position(buf.position() + ServerProtocol.DOUBLE_TO_BYTES_LENGTH);
                position++;
            }
            return new ServerCommand(timeSent, objectID, objectStats);

        } catch (IOException e) {
            System.err.println("Bad index...");
        }
        return null;
    }

}
