package com.knightlore.network.protocol;

import java.nio.ByteBuffer;

import com.knightlore.engine.GameEngine;

public class NetworkUtils {

    /**
     * Put a String into a ByteBuffer.
     * 
     * @param buf
     *            The ByteBuffer to insert into.
     * @param string
     *            The String to insert.
     */
    public static void putStringIntoBuf(ByteBuffer buf, String string) {
        byte[] bytes = string.getBytes(GameEngine.CHARSET);
        buf.putInt(bytes.length);
        buf.put(bytes);
    }

    /**
     * Gets a String from a ByteBuffer.
     * 
     * @param buf
     *            The ByteBuffer to use.
     * @return The extracted String.
     */
    public static String getStringFromBuf(ByteBuffer buf) {
        int len = buf.getInt();
        byte[] bytes = new byte[len];
        buf.get(bytes, 0, len);
        return new String(bytes, GameEngine.CHARSET);
    }
}
