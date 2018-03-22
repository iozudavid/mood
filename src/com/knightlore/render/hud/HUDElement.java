package com.knightlore.render.hud;

import com.knightlore.game.entity.Player;
import com.knightlore.render.IRenderable;

public abstract class HUDElement implements IRenderable {
    
    protected Player player;
    protected long anim = 0;
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
}
