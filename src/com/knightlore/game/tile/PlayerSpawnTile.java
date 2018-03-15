package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Team;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.Push;
import com.knightlore.game.buff.Slow;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.utils.Vector2D;

public class PlayerSpawnTile extends Tile {
    private final Team team;
    private static PlayerSpawnTile instanceBlue = new PlayerSpawnTile(Team.blue);
    private static PlayerSpawnTile instanceRed = new PlayerSpawnTile(Team.red);
    private static PlayerSpawnTile instanceNone = new PlayerSpawnTile(Team.none);
    
    private PlayerSpawnTile(Team team) {
        this.team = team;
    }

    public static PlayerSpawnTile getInstance(Team t) {
        switch(t){
            case blue : return instanceBlue;
            case red  : return instanceRed;
            default   : return instanceNone;
        }
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
        // just for testing the fire debuff
        entity.resetBuff(new Fire(entity));
    }
}
