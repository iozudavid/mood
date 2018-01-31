package com.knightlore.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerProtocol {

	 // The number of bytes taken up at the start of each packet with metadata.
    public static final int METADATA_LENGTH = 8;
    
    // The number of bytes taken up after metadata of each packet with playerid.
    public static final int PLAYERID_LENGTH = 16;
    
    public static final int MESSAGE_STARTING_POINT = METADATA_LENGTH + PLAYERID_LENGTH;
    
    // double will have exactly 18 entries in array
    //when serialize it
    public static final int DOUBLE_TO_BYTES_LENGTH = 18;
    
    private static final Map<Integer, ServerControl> positionAction;
    static {
        positionAction = new HashMap<Integer, ServerControl>();
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
    //array of length 2: array[0] = position from
    //                   array[1] = position to
    //construct the server data
    private static final Map<Integer[], ServerControl> indexAction;
    static {
        indexAction = new HashMap<Integer[], ServerControl>();
        try {
        	//mentain 18 entries for each element
			indexAction.put(new Integer[]{0, DOUBLE_TO_BYTES_LENGTH}, ServerControl.XPOS);               // 0, 18
			indexAction.put(new Integer[]{DOUBLE_TO_BYTES_LENGTH + 1, (getPositionByControl(ServerControl.YPOS) + 1) * DOUBLE_TO_BYTES_LENGTH + 1}, ServerControl.YPOS); //19, 37
			indexAction.put(new Integer[]{DOUBLE_TO_BYTES_LENGTH*2 + 2, (getPositionByControl(ServerControl.XDIR) + 1) * DOUBLE_TO_BYTES_LENGTH + 2}, ServerControl.XDIR); //38, 56
			indexAction.put(new Integer[]{DOUBLE_TO_BYTES_LENGTH*3 + 3, (getPositionByControl(ServerControl.YDIR) + 1) * DOUBLE_TO_BYTES_LENGTH + 3}, ServerControl.YDIR); //57, 75
			indexAction.put(new Integer[]{DOUBLE_TO_BYTES_LENGTH*4 + 4, (getPositionByControl(ServerControl.XPLANE) + 1) * DOUBLE_TO_BYTES_LENGTH + 4}, ServerControl.XPLANE);
			indexAction.put(new Integer[]{DOUBLE_TO_BYTES_LENGTH*5 + 5, (getPositionByControl(ServerControl.YPLANE) + 1) * DOUBLE_TO_BYTES_LENGTH + 5}, ServerControl.YPLANE);
			// just this for now
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public static final int TOTAL_LENGTH = METADATA_LENGTH + PLAYERID_LENGTH + indexAction.size();

    // get the key by passing index in the array
    public static ServerControl getIndexesByPosition(int i) throws IOException {
        if (i > indexAction.size())
            throw new IOException();
        for(Integer[] element : indexAction.keySet()){
        	if(i>=element[0] && i<=element[1])
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

    // get the index by passing the key
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
    
	public static UUID asUuid(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		long firstLong = byteBuffer.getLong();
		long secondLong = byteBuffer.getLong();
		return new UUID(firstLong, secondLong);
	}

	public static byte[] asBytes(UUID uuid) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}
	
}
