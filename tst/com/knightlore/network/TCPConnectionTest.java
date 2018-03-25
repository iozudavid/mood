package com.knightlore.network;

import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(System.class)
public class TCPConnectionTest {
   
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    
    @Before
    public void setUp(){
        socket = Mockito.mock(Socket.class);
        is = PowerMockito.mock(DataInputStream.class);
        os = PowerMockito.mock(DataOutputStream.class);
        try {
            PowerMockito.when(socket.getInputStream()).thenReturn(is);
            PowerMockito.when(socket.getOutputStream()).thenReturn(os);
            PowerMockito.whenNew(DataInputStream.class).withArguments(Matchers.any(InputStream.class)).thenReturn(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_send(){
         TCPConnection c = new TCPConnection(socket);
         ByteBuffer bb = ByteBuffer.allocate(4);
         bb.putInt(1);
         c.send(bb);
         try {
            Mockito.verify(os,Mockito.times(1)).write(bb.array(), 0, 4);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_receive_null(){
       
        TCPConnection conn = new TCPConnection(socket);
        try {
            PowerMockito.when(is.readInt()).thenReturn(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(conn.receiveBlocking()==null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
