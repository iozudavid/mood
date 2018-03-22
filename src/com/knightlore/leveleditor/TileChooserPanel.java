package com.knightlore.leveleditor;

import com.google.common.collect.ImmutableList;
import com.knightlore.game.Team;
import com.knightlore.game.entity.pickup.PickupType;
import com.knightlore.game.tile.*;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TileChooserPanel extends JPanel {
    private static final ImmutableList<Tile> OPTIONS = ImmutableList.of(
            AirTile.getInstance(),
            new BrickTile(),
            new BushTile(),
            new LavaTile(),
            new MossBrickTile(),
            new PickupTile(PickupType.SHOTGUN),
            new PickupTile(PickupType.HEALTH),
            new PlayerSpawnTile(Team.BLUE),
            new PlayerSpawnTile(Team.RED),
            new TurretTile(Team.BLUE),
            new TurretTile(Team.RED),
            new TurretTile(Team.NONE)
    );

    public TileChooserPanel() {
        super();
        Box box = Box.createVerticalBox();
        for (Tile o : OPTIONS) {
            JButton button = new JButton(o.toString());
            button.addActionListener(e -> LevelEditorWindow.pen.stroke = o);
            box.add(button);
        }

        add(box);
    }
}
