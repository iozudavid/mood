package com.knightlore.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.engine.GameWorld;
import com.knightlore.game.Player;
import com.knightlore.game.area.AreaFactory;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Mob;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.gui.Button;
import com.knightlore.gui.GUICanvas;
import com.knightlore.render.Camera;
import com.knightlore.render.Environment;
import com.knightlore.utils.Vector2D;

public class TestWorld extends GameWorld {

	private List<Mob> mobs;

	private Player player;
	private Camera mainCamera;
	private GUICanvas gui;

	public TestWorld() {
		// init all the variables
		mobs = new ArrayList<Mob>();
	}

	@Override
	public void initWorld() {
		// First create the map
		map = AreaFactory.createRandomMap(Environment.LIGHT_OUTDOORS);

		// now populate the world
		mainCamera = new Camera(4.5, 4.5, 1, 0, 0, Camera.FIELD_OF_VIEW, map);
	}

	@Override
	public void populateWorld() {
		// add the player and mobs
		player = new Player(mainCamera);
		mobs.add(new ShotgunPickup(new Vector2D(20, 20)));
		mobs.add(new Zombie(1, new Vector2D(21, 20)));

		// setup testing ui
		gui = new GUICanvas();
		Button b = new Button(5, 5, 0);
		b.rect.width = 100;
		b.rect.height = 30;
		gui.addGUIObject(b);

		// add pickups
		for (int i = 1; i < 5; i += 2) {
			mobs.add(new ShotgunPickup(new Vector2D(i, 3)));
		}
	}

	@Override
	public GameWorld loadFromFile(String fileName) {
		System.out.println("Loading Not implemented!");
		return null;
	}

	@Override
	public boolean saveToFile(String fileName) {
		System.out.println("Saving Not implemented!");
		return false;
	}

	public List<Mob> getMobs() {
		return mobs;
	}

	public Map getMap() {
		return map;
	}

}
