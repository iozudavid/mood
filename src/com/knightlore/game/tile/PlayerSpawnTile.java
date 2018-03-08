package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class PlayerSpawnTile extends Tile {
    private final Team team;

    public PlayerSpawnTile(Team team) {
        this.team = team;
    }

    @Override
    public Graphic getTexture() {
        return Texture.BUSH;
    }

    @Override
    public double getOpacity() {
        return 0.5 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
    }

    @Override
    public double getSolidity() {
        return 0.1D;
    }

    @Override
    public int getMinimapColor() {
        return 0x00FF00;
    }

    @Override
    public void onShot() {
    }

    public char toChar() {
        if (team == Team.none) {
            return '0';
        }else if(team == Team.blue) {
            return '1';
        }else {
            return '2';
        }
    }

    public Tile reflectTileX() {
        if (team == Team.none) {
            return new PlayerSpawnTile(Team.none);
        } else if (team == Team.blue){
            return new PlayerSpawnTile(Team.red);
        } else {
            return new PlayerSpawnTile(Team.blue);
        }
    }

    public Tile reflectTileY() {
        if (team == Team.none) {
            return new PlayerSpawnTile(Team.none);
        } else if (team == Team.blue) {
            return new PlayerSpawnTile(Team.red);
        } else {
            return new PlayerSpawnTile(Team.blue);
        }
    }
    
    @Override
    public boolean blockLOS() {
        return true;
    }
    
    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public Tile copy() {
        return new PlayerSpawnTile(team);
    }

    @Override
    public void onEntered(Entity entity) {
    }
}
