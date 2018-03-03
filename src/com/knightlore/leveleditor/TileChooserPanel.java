package com.knightlore.leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.knightlore.game.tile.TileType;

public class TileChooserPanel extends JPanel {

    public TileChooserPanel() {
        super();
        Box box = Box.createVerticalBox();

        TileType[] options = TileType.values();
        for (TileType o : options) {
            JButton button = new JButton(o.name());
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    LevelEditorWindow.pen.stroke = o;
                }
            });
            box.add(button);
        }

        add(box);
    }

}
