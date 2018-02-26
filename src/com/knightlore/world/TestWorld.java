package com.knightlore.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.GameSettings;
import com.knightlore.game.Player;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.gui.Button;
import com.knightlore.gui.GUICanvas;
import com.knightlore.gui.TextField;
import com.knightlore.render.Camera;
import com.knightlore.render.Environment;
import com.knightlore.render.minimap.IMinimapObject;
import com.knightlore.utils.Vector2D;

public class TestWorld extends GameWorld {

    private List<Entity> ents;
    private List<IMinimapObject> minimapObjs;

    private GUICanvas gui;

    public TestWorld() {
        super();
        // init all the variables
        ents = new ArrayList<Entity>();
        minimapObjs = new ArrayList<IMinimapObject>();
    }

    @Override
    public void initWorld() {
        // First create the map
        MapGenerator generator = new MapGenerator();
        // FIXME don't hardcode the seed...
        this.map = generator.createMap(16, 16, 161803398875L);
        // FIXME hack hack hack, this is just for the demo
        GameSettings.spawnPos = map.getRandomSpawnPoint();

        new Camera(getMap());
    }

    @Override
    public void populateWorld() {
        if (GameSettings.isServer()) {
            // add the mobs
            ShotgunPickup shot = new ShotgunPickup(new Vector2D(8, 8));
            shot.init();
            ents.add(shot);
            Zombie zom = new Zombie(1, new Vector2D(4, 5));
            zom.init();
            ents.add(zom);
            // add pickups
            for (int i = 5; i < 9; i += 2) {
                ShotgunPickup shotI = new ShotgunPickup(new Vector2D(i, 3));
                shotI.init();
                ents.add(shotI);
            }
        }

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

    public List<Entity> getEntities() {
        return ents;
    }

    @Override
    public Player createPlayer() {
        Vector2D pos = getMap().getRandomSpawnPoint();
        Player player = new Player(pos, Vector2D.UP);
        player.init();
        ents.add(player);
        minimapObjs.add(player);
        return player;
    }

    @Override
    public void addEntity(Entity ent) {
        this.ents.add(ent);
    }

    @Override
    public void removeEntity(Entity ent) {
        this.ents.remove(ent);
    }

    @Override
    public Environment getEnvironment() {
        return Environment.DUNGEON;
    }

}
