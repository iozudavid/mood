package com.knightlore.leveleditor;

import com.knightlore.game.Team;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.ActionEvent;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TileButtonTest {
    
    private int x;
    private int y = 2;
    private Tile expectedButtonTile;
    private Map map;
    
    @Before
    public void init() {
        x = 1;
        y = 2;
        expectedButtonTile = new LavaTile();
        
        Tile[][] grid = {
                {new BrickTile(), new BrickTile(), new MossBrickTile(), new BrickTile()},
                {new BrickTile(), AirTile.getInstance(), AirTile.getInstance(), new BrickTile()},
                {new BrickTile(), AirTile.getInstance(), new LavaTile(), new MossBrickTile()},
                {new BrickTile(), AirTile.getInstance(), new LavaTile(), new PlayerSpawnTile(Team.BLUE)}
        };
        grid[x][y] = expectedButtonTile;
        
        long seed = 1287101289412L;
        map = new Map(grid, seed);
    }
    
    @Test
    public void getTile() {
        // Given
        LevelEditorPanel panel = mock(LevelEditorPanel.class);
        TileButton button = new TileButton(panel, map, x, y);
        
        // When
        Tile buttonTile = button.getTile();
        
        // Then
        assertThat(buttonTile, is(expectedButtonTile));
    }
    
    @Test
    public void actionPerformed() {
        // Given
        LevelEditorPanel panel = mock(LevelEditorPanel.class);
        TileButton button = new TileButton(panel, map, x, y);
        
        // When
        ActionEvent action = mock(ActionEvent.class);
        button.actionPerformed(action);
        Tile newExpectedTile = LevelEditorWindow.pen.getTile();
        Color expectedColor = new Color(newExpectedTile.getMinimapColor());
        
        // Then
        assertThat(button.getTile(), is(LevelEditorWindow.pen.getTile()));
        assertThat(button.getBackground(), is(expectedColor));
        assertThat(button.getForeground(), is(expectedColor));
        
    }
}