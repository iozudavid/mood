package com.knightlore.network.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NetworkUtils {
    // Checks if two byte arrays representing serialised object states are the
    // same (i.e. ignores the metadata/timestamp when comparing).
    public static boolean areStatesDifferent(byte[] x, byte[] currentState) {
        if (x == null || currentState == null)
            return true;
        byte[] lastStateWithoutTimeToCompare = Arrays.copyOfRange(x,
                ServerProtocol.METADATA_LENGTH, ServerProtocol.TOTAL_LENGTH);
        byte[] currentStateWithoutTimeToCompare = Arrays.copyOfRange(
                currentState, ServerProtocol.METADATA_LENGTH,
                ServerProtocol.TOTAL_LENGTH);
        if (lastStateWithoutTimeToCompare.length != currentStateWithoutTimeToCompare.length)
            return true;
        for (int i = 0; i < lastStateWithoutTimeToCompare.length; i++) {
            if (lastStateWithoutTimeToCompare[i] != currentStateWithoutTimeToCompare[i])
                return true;
        }
        return false;

    }

    // Puts a String into a ByteBuffer.
    public static void putStringIntoBuf(ByteBuffer buf, String string) {
        int len = string.length();
        buf.putInt(len);
        for (int i = 0; i < len; i++)
            buf.putChar(string.charAt(i));
    }

    // Gets a String from a ByteBuffer.
    public static String getStringFromBuf(ByteBuffer buf)  {
        int len = buf.getInt();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++)
            chars[i] = buf.getChar();
        return new String(chars);
    }
}
