package com.knightlore.network.server;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GameEngine.class)
public class ReceiveTest {

    private Connection conn;
    private Receive rec;
    private GameEngine ge;
    private NetworkObjectManager nom;
    
    @Before
    public void setUp(){
        nom = Mockito.mock(NetworkObjectManager.class);
        ge = Mockito.mock(GameEngine.class);
        conn = Mockito.mock(Connection.class);
        rec = new Receive(conn);
    }
    
    @Test(expected = NullPointerException.class)
    public void test_receive() throws Exception{
        PowerMockito.when(conn.receive()).thenReturn(ByteBuffer.allocate(100));
        PowerMockito.mockStatic(GameEngine.class);
        PowerMockito.when(GameEngine.getSingleton()).thenReturn(ge);
        PowerMockito.when(ge.getNetworkObjectManager()).thenReturn(nom);
        PowerMockito.doNothing().when(nom, "processMessage", (Matchers.any(ByteBuffer.class)));
        rec.run();
    }
    
}
