package com.knightlore.leveleditor;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.knightlore.game.area.Map;

public class LevelEditorPanel extends JPanel {
    private Map map;
    private TileButton[][] tileButtons;

    public LevelEditorPanel(Map map) {
        super();
        initialise(map);
    }

    public void initialise(Map map) {
        this.map = map;
        this.tileButtons = new TileButton[map.getHeight()][map.getWidth()];

        setLayout(new GridLayout(map.getHeight(), map.getWidth()));
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                tileButtons[y][x] = new TileButton(this, map, x, y);
                add(tileButtons[y][x]);
            }
        }
    }

    public Map getMap() {
        return map;
    }
}
