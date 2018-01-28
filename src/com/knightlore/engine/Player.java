package com.knightlore.engine;

import com.knightlore.render.IRenderable;
import com.knightlore.render.Screen;
import com.knightlore.utils.Vector2D;

public abstract class Player extends GameObject implements IRenderable {

    public Player(Vector2D position) {
        super(position);
//        this.xDir = xDir;
//        this.yDir = yDir;
//        this.xPlane = xPlane;
//        this.yPlane = yPlane;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(Screen s, int x, int y) {
        s.fillRect(0x000000, (int) this.position.getX(),
                (int) this.position.getY(), 10, 50);
    }

}
