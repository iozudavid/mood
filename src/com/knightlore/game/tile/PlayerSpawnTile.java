package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Team;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.Push;
import com.knightlore.game.buff.Slow;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.utils.Vector2D;

public class PlayerSpawnTile extends Tile {
    private final Team team;
    private static PlayerSpawnTile instanceBlue = new PlayerSpawnTile(Team.blue);
    private static PlayerSpawnTile instanceRed = new PlayerSpawnTile(Team.red);
    private static PlayerSpawnTile instanceNone = new PlayerSpawnTile(Team.none);
    
    private static Vector2D pushVector = Vector2D.DOWN;
    
    private static Animation RED_LAVA_ANIM = new Animation((long) (GameEngine.UPDATES_PER_SECOND / 4));
    static {
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F1);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F2);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F3);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F4);
        GameEngine.ticker.addTickListener(RED_LAVA_ANIM);
    }
    
    private static Animation BLUE_LAVA_ANIM = new Animation((long) (GameEngine.UPDATES_PER_SECOND / 4));
    static {
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F1);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F2);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F3);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F4);
        GameEngine.ticker.addTickListener(BLUE_LAVA_ANIM);
    }
    
    public PlayerSpawnTile(Team team) {
        this.team = team;
    }
    
    @Override
    public Graphic getWallTexture() {
        if( team == Team.blue) {
            return Texture.BLUE_BUSH;
        }
        if( team == Team.red) {
            return Texture.RED_BUSH;
        }
        return Texture.BUSH;
    }

    @Override
    public Graphic getFloorTexture() {
        if( team == Team.blue) {
            return BLUE_LAVA_ANIM.getGraphic();
        }
        if( team == Team.red) {
            return RED_LAVA_ANIM.getGraphic();
        }
        return Texture.BUSH;
    }
    
    @Override
    public double getOpacity() {
        //return 0.5 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
        return 0.37 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
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
        entity.resetBuff(new Push(entity, pushVector));
    }
}
