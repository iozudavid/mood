package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.Team;
import com.knightlore.render.graphic.Graphic;

public class TurretTile extends Tile {

    Team team = Team.none;
    
    public TurretTile(Team t) {
        team = t;
    }
    
    @Override
    public Graphic getTexture() {
        return AirTile.getInstance().getTexture();
    }

    @Override
    public void onShot() {
        // Maybe play some turrety noise??
    }

    @Override
    public void onEntered(Player player) {
        // Unsure
    }
    
    @Override
    public char toChar() {
        return 'T';
    }

}
