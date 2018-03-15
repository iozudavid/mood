package com.knightlore.game.tile;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;

public class TurretTile extends Tile {
    private final Team team;

    public TurretTile(Team team) {
        this.team = team;
    }

    @Override
    public Graphic getWallTexture() {
        return AirTile.getInstance().getWallTexture();
    }

    @Override
    public double getSolidity() {
        return 0.1D;
    }

    @Override
    public void onShot() {
        // Maybe play some turrety noise??
    }

    @Override
    public void onEntered(Entity entity) {
    }

    @Override
    public char toChar() {
        return 'T';
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public Tile copy() {
        return new TurretTile(team);
    }
    
    @Override
    public double getOpacity() {
        return 1D;
    }
    
}
