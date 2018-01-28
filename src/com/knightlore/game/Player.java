package com.knightlore.game;

import java.util.Map;

import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.render.Camera;
import com.knightlore.render.IRenderable;
import com.knightlore.render.Screen;
import com.knightlore.utils.Vector2D;

public class Player extends GameObject implements IRenderable {

	private Camera camera;
	
	public Player(Camera camera) {
		this.camera = camera;
	}

	public Vector2D getPosition() {
		Vector2D pos = new Vector2D(camera.getxPos(), camera.getyPos());
		return pos;
	}

	public Vector2D getDirection() {
		Vector2D dir = new Vector2D(camera.getxDir(), camera.getyDir());
		return dir;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void setInputState(Map<ClientControl, Byte> inputState) {
	    camera.setInputState(inputState);
	}

    @Override
    public void render(Screen s, int x, int y) {
        s.fillRect(0x000000, (int) this.position.getX(),
                (int) this.position.getY(), 10, 50);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdate() {
        camera.update();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }

}
