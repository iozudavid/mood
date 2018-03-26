package com.knightlore.utils.physics;


import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.world.GameWorld;
import com.knightlore.utils.Vector2D;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GameEngine.class)
public class PhysicsTest {
    
    private Map m;
    @Mock
    private GameEngine ge;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(GameEngine.class);
        PowerMockito.when(GameEngine.getSingleton()).thenReturn(ge);
    }
    
    @Test
    public void test_point_in_rect() {
        Vector2D pos = new Vector2D(1.2, 1.5);
        Rectangle rect = new Rectangle(1, 1, 1, 1);
        Vector2D pos2 = new Vector2D(1.2, 1.5);
        Rectangle rect2 = new Rectangle(0, 0, 1, 1);
        
        assertThat(Physics.pointInAWTRectangleTest(pos, rect), is(true));
        assertThat(Physics.pointInAWTRectangleTest(pos2, rect2), is(false));
    }
    
    @Test(expected = IllegalStateException.class)
    public void test_linecast_segment_less_than_0() {
        Physics.linecastQuick(Vector2D.ZERO, Vector2D.ZERO, 0);
    }
    
    @Test
    public void test_linecast_true() {
        Tile t = PowerMockito.mock(Tile.class);
        GameWorld w = PowerMockito.mock(GameWorld.class);
        m = PowerMockito.mock(Map.class);
        PowerMockito.when(ge.getWorld()).thenReturn(w);
        PowerMockito.when(w.getMap()).thenReturn(m);
        PowerMockito.when(m.getTile(Matchers.anyInt(), Matchers.anyInt())).thenReturn(t);
        PowerMockito.when(t.blockLOS()).thenReturn(true);
        boolean linecast = Physics.linecastQuick(new Vector2D(1.1, 1.5), new Vector2D(1.2, 1.5), 2);
        assertThat(linecast, is(true));
    }
    
    @Test
    public void test_linecast_false() {
        Tile t = PowerMockito.mock(Tile.class);
        GameWorld w = PowerMockito.mock(GameWorld.class);
        m = PowerMockito.mock(Map.class);
        PowerMockito.when(ge.getWorld()).thenReturn(w);
        PowerMockito.when(w.getMap()).thenReturn(m);
        PowerMockito.when(m.getTile(Matchers.anyInt(), Matchers.anyInt())).thenReturn(t);
        PowerMockito.when(t.blockLOS()).thenReturn(false);
        boolean linecast = Physics.linecastQuick(new Vector2D(1.1, 1.5), new Vector2D(1.2, 1.5), 2);
        assertThat(linecast, is(false));
    }
    
}
