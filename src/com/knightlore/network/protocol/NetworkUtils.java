package com.knightlore.network.protocol;

import java.nio.ByteBuffer;

public class NetworkUtils {
	
    // Puts a String into a ByteBuffer.
    public static void putStringIntoBuf(ByteBuffer buf, String string) {
        int len = string.length();
        buf.putInt(len);
        for (int i = 0; i < len; i++)
            buf.putChar(string.charAt(i));
    }

    // Gets a String from a ByteBuffer.
    public static String getStringFromBuf(ByteBuffer buf) {
        int len = buf.getInt();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++)
            chars[i] = buf.getChar();
        return new String(chars);
    }
}
