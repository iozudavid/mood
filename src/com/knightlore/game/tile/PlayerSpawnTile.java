package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.Team;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class PlayerSpawnTile extends Tile {

    public Team team = Team.none;

    public PlayerSpawnTile(Team t) {
        team = t;
    }

    @Override
    public Graphic getTexture() {
        return Texture.BUSH;
    }

    @Override
    public double getOpacity() {
        double opacity = 0.5 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
        return opacity;
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

    @Override
    public void onEntered(Player p) {

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
    public TileType getTileType() {
        return TileType.spawn;
    }

}
