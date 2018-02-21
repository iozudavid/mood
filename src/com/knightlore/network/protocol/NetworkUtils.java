package com.knightlore.network.protocol;

import java.nio.ByteBuffer;

public class NetworkUtils {

	//check if 2 bytebuffers are equals
	public static boolean areByteBuffersEquals(ByteBuffer bf1, ByteBuffer bf2){
		//store for both bytebuffers
		//the current position
		//in order to set it back when the method finishes
		final int bf1Pos = bf1.position();
		final int bf2Pos = bf2.position();
		//check the entire bytebuffer
		//if it's equals
		bf1.position(0);
		bf2.position(0);
		if(bf1.capacity() != bf2.capacity())
			return false;
		while(bf1.hasRemaining()){
			byte byteBf1 = bf1.get();
			byte byteBf2 = bf2.get();
			if(byteBf1!=byteBf2)
				return false;
		}
		//set the initial positions back
		bf1.position(bf1Pos);
		bf2.position(bf2Pos);
		return true;
	}
	
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
