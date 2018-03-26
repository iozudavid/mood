package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GameEngine.class})
public class SendToClientTest {
    
    @Mock
    private GameEngine ge;
    private Connection conn;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(GameEngine.class);
        PowerMockito.when(GameEngine.getSingleton()).thenReturn(ge);
    }
    
    @Test
    public void test_stop_sending_packet() {
        ServerNetworkObjectManager serverManager = PowerMockito.mock(ServerNetworkObjectManager.class);
        PowerMockito.when(ge.getNetworkObjectManager()).thenReturn(serverManager);
        PowerMockito.when(serverManager.registerClientSender(Matchers.any(SendToClient.class))).thenReturn(UUID.randomUUID());
        conn = PowerMockito.mock(Connection.class);
        SendToClient s = new SendToClient(conn);
        PowerMockito.doNothing().when(conn).send(Matchers.any(ByteBuffer.class));
        PowerMockito.when(conn.getTerminated()).thenReturn(true);
        s.send(ByteBuffer.allocate(100));
        s.run();
        Mockito.verify(conn, Mockito.times(0)).send(Matchers.any(ByteBuffer.class));
    }
    
}
