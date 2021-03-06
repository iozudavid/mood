package com.knightlore.game.tile;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;

public class TurretTile extends Tile {
    private final Team team;
    
    public TurretTile(Team team) {
        this.team = team;
        pathable = false;
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
    public double getOpacity() {
        return 0.0D;
    }
    
    @Override
    public double getCost() {
        return 10D / (1D - getSolidity());
    }
    
    @Override
    public void onEntered(Entity entity) {
    }
    
    @Override
    public int getMinimapColor() {
        return team.getColor();
    }
    
    @Override
    public String toString() {
        return team.toString() + " turret";
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
    
}
