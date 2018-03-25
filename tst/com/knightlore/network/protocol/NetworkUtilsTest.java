package com.knightlore.network.protocol;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class NetworkUtilsTest {
    
    private String test = "test";
    private ByteBuffer bb;
    
    @Before
    public void setUp(){
        bb = ByteBuffer.allocate(100);
    }
    
    @Test
    public void test_put_string(){
        NetworkUtils.putStringIntoBuf(bb, test);
        bb.rewind();
        assertEquals(bb.getInt(), test.getBytes().length);
        byte[] testBytes = new byte[test.getBytes().length];
        bb.get(testBytes, 0, test.getBytes().length);
        assertEquals(new String(testBytes),test);
    }
    
    @Test
    public void test_get_string(){
        NetworkUtils.putStringIntoBuf(bb, test);
        bb.rewind();
        assertEquals(NetworkUtils.getStringFromBuf(bb),test);
    }
}
