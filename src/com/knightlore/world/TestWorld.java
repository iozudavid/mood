package com.knightlore.world;

import com.knightlore.GameSettings;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.gui.Button;
import com.knightlore.gui.GUICanvas;
import com.knightlore.gui.TextField;
import com.knightlore.render.Camera;
import com.knightlore.render.Environment;
import com.knightlore.utils.Vector2D;

public class TestWorld extends GameWorld {
    private Camera mainCamera;
    private GUICanvas gui;

    public TestWorld() {
        super(Environment.DUNGEON);
    }

    @Override
    public void initWorld() {
        // First create the map
        MapGenerator generator = new MapGenerator();
        // FIXME don't hardcode the seed...
        map = generator.createMap(16, 16, 161803398875L);
        // FIXME hack hack hack, this is just for the demo
        GameSettings.spawnPos = map.getRandomSpawnPoint();

        // now populate the world
        Vector2D pos = GameSettings.spawnPos;
        mainCamera = new Camera(pos.getX(), pos.getY(), 1, 0, 0, Camera.FIELD_OF_VIEW, map);
    }

    @Override
    public void populateWorld() {
        // add the player and mobs
        entities.add(new ShotgunPickup(new Vector2D(20, 20)));
        entities.add(new Zombie(1, new Vector2D(21, 20)));

        if (GameSettings.isClient()) {
            // setup testing ui
            gui = new GUICanvas();
            Button b = new Button(5, 5, 0);
            b.rect.width = 100;
            b.rect.height = 30;
            TextField tf = new TextField(100, 100, 0, "Sample Text");
            tf.rect.width = 150;
            tf.rect.height = 30;
            gui.addGUIObject(tf);
            gui.addGUIObject(b);
            // Renderer.setGUI(gui);
        }

        // add pickups
        for (int i = 1; i < 5; i += 2) {
            entities.add(new ShotgunPickup(new Vector2D(i, 3)));
        }
    }

    @Override
    public void updateWorld() {

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
}
