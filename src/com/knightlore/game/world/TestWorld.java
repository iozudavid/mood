package com.knightlore.game.world;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.GameSettings;
import com.knightlore.ai.AIManager;
import com.knightlore.ai.TurretServer;
import com.knightlore.ai.TurretShared;
import com.knightlore.game.Player;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.game.menus.Menus;
import com.knightlore.gui.Button;
import com.knightlore.gui.GUICanvas;
import com.knightlore.gui.GUIPanel;
import com.knightlore.gui.TextField;
import com.knightlore.render.Camera;
import com.knightlore.render.Environment;
import com.knightlore.utils.Vector2D;

public class TestWorld extends GameWorld {
    private final List<Entity> ents = new ArrayList<>();
    private GUICanvas gui;
    
    public TestWorld() {
        super();
    }
    
    @Override
    public void initWorld() {
        // First create the map
        MapGenerator generator = new MapGenerator();
        // FIXME don't hardcode the seed...
        this.map = generator.createMap(16, 16, 161803398875L);
        // FIXME hack hack hack, this is just for the demo
        GameSettings.spawnPos = map.getRandomSpawnPoint();
        
        // as of the moment, aiManager will do pathfinding for AI Bots
        aiManager = new AIManager(map);
        new Camera(getMap());
    }
    
    @Override
    public void populateWorld() {
        if (GameSettings.isServer()) {
            // add the mobs
            ShotgunPickup shot = new ShotgunPickup(new Vector2D(8, 8));
            shot.init();
            ents.add(shot);
            Zombie zom = new Zombie(new Vector2D(4, 5));
            zom.init();
            ents.add(zom);
            // add pickups
            for (int i = 5; i < 9; i += 2) {
                ShotgunPickup shotI = new ShotgunPickup(new Vector2D(i, 3));
                shotI.init();
                ents.add(shotI);
            }
            TurretShared tboi = new TurretServer(3, GameSettings.spawnPos, Vector2D.UP);
            tboi.init();
        }

        if (GameSettings.isClient()) {
            // setup testing ui
            gui = new GUICanvas();
            GUIPanel optionsPanel = new GUIPanel(0,0,150,150);
            Button b = new Button(5, 5,100,30);
            optionsPanel.AddGUIObject(b);
            Menus.getOptionsMenu().setPanel(optionsPanel);
            b.clickFunction = Menus.getOptionsMenu()::openMenu;
            TextField tf = new TextField(100, 100, 150,30);
            tf.setText("Sample Text");
            tf.rect.width = 150;
            tf.rect.height = 30;
            gui.addGUIObject(tf);
            gui.addGUIObject(b);
            //Renderer.setGUI(gui);
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
        playerManager.addPlayer(player);
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
        return Environment.LIGHT_OUTDOORS;
    }
}
