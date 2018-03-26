package com.knightlore.network.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class NetworkUtilsTest {
    
    private static final String TEST = "test";
    private ByteBuffer bb;
    
    @Before
    public void setUp(){
        bb = ByteBuffer.allocate(100);
    }
    
    @Test
    public void test_put_string(){
        NetworkUtils.putStringIntoBuf(bb, TEST);
        bb.rewind();
        assertEquals(bb.getInt(), TEST.getBytes().length);
        byte[] testBytes = new byte[TEST.getBytes().length];
        bb.get(testBytes, 0, TEST.getBytes().length);
        assertEquals(new String(testBytes), TEST);
    }
    
    @Test
    public void test_get_string(){
        NetworkUtils.putStringIntoBuf(bb, TEST);
        bb.rewind();
        assertEquals(NetworkUtils.getStringFromBuf(bb), TEST);
    }
}
