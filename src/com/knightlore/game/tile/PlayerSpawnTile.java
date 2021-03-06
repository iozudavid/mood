package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Team;
import com.knightlore.game.buff.Push;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.animation.TimedAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;
import com.knightlore.utils.Vector2D;

public class PlayerSpawnTile extends Tile {
    private static final TimedAnimation<Graphic> RED_LAVA_ANIM = new TimedAnimation<>(
            (long)(GameEngine.UPDATES_PER_SECOND / 4));
    private static final TimedAnimation<Graphic> BLUE_LAVA_ANIM = new TimedAnimation<>(
            (long)(GameEngine.UPDATES_PER_SECOND / 4));
    
    static {
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F1);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F2);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F3);
        RED_LAVA_ANIM.addFrame(Texture.RED_LAVA_F4);
        GameEngine.ticker.addTickListener(RED_LAVA_ANIM);
    }
    
    static {
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F1);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F2);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F3);
        BLUE_LAVA_ANIM.addFrame(Texture.BLUE_LAVA_F4);
        GameEngine.ticker.addTickListener(BLUE_LAVA_ANIM);
    }
    
    private final Team team;
    private final Vector2D pushVector;
    
    public PlayerSpawnTile(Team team) {
        this.team = team;
        pushVector = Vector2D.UP;
        pathable = false;
    }
    
    public PlayerSpawnTile(Team team, Vector2D v) {
        this.team = team;
        pushVector = v;
        pathable = false;
    }
    
    @Override
    public Graphic getWallTexture() {
        if (team == Team.BLUE) {
            return Texture.BLUE_BUSH;
        }
        if (team == Team.RED) {
            return Texture.RED_BUSH;
        }
        return Texture.BUSH;
    }
    
    @Override
    public Graphic getFloorTexture() {
        if (team == Team.BLUE) {
            return BLUE_LAVA_ANIM.getFrame();
        }
        if (team == Team.RED) {
            return RED_LAVA_ANIM.getFrame();
        }
        return Texture.BUSH;
    }
    
    @Override
    public double getCost() {
        return 100 / (1D - getSolidity());
    }
    
    @Override
    public double getOpacity() {
        return 0.2 + (Math.sin(GameEngine.ticker.getTime() * 0.05)) / 4;
    }
    
    @Override
    public double getSolidity() {
        return 0.1D;
    }
    
    @Override
    public int getMinimapColor() {
        return team.getColor();
    }
    
    @Override
    public String toString() {
        return team.toString() + " spawn";
    }
    
    @Override
    public char toChar() {
        switch (team) {
            case NONE:
                return '0';
            case BLUE:
                return '1';
            default:
                return '2';
        }
    }
    
    public Tile reflectTileY() {
        Team teamR;
        switch (team) {
            case BLUE:
                teamR = Team.RED;
                break;
            case RED:
                teamR = Team.BLUE;
                break;
            default:
                teamR = Team.NONE;
        }
        Vector2D pushVectorR =
                new Vector2D(pushVector.getX() * -1, pushVector.getY());
        return new PlayerSpawnTile(teamR, pushVectorR);
        
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
