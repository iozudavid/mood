package com.knightlore.leveleditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;

public class TileButton extends JButton implements ActionListener {
    
    private final LevelEditorPanel pane;
    private final Map map;
    private final int x, y;
    
    public TileButton(LevelEditorPanel pane, Map map, int x, int y) {
        this.pane = pane;
        this.map = map;
        this.x = x;
        this.y = y;
        setOpaque(true);
        setBorderPainted(false);
        setTile();
        addActionListener(this);
    }
    
    public Tile getTile() {
        return map.getTile(x, y);
    }
    
    public void actionPerformed(ActionEvent e) {
        map.setTile(LevelEditorWindow.pen.getTile(), x, y);
        setTile();
        pane.repaint();
    }
    
    private void setTile() {
        int color = getTile().getMinimapColor();
        Color c = new Color(color);
        setBackground(c);
        setForeground(c);
    }
}
