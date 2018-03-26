package com.knightlore.render;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.gui.GUICanvas;
import com.knightlore.gui.GUIState;
import com.knightlore.gui.GameChat;
import com.knightlore.gui.MultiplayerMenu;
import com.knightlore.gui.SettingsMenu;
import com.knightlore.gui.StartMenu;
import com.knightlore.render.hud.HUD;
import com.knightlore.render.minimap.Minimap;

/**
 * The display represents everything that is rendered onto the game canvas. The
 * game renderer, minimap and HUD are encapsulated here. The Display class is
 * responsible for compositing the individual screen components onto the main
 * pixel buffer.
 *
 * @author Joe Ellis
 */
public class Display implements IRenderable {
    
    private final List<GUICanvas> guis;
    /**
     * The renderer -- renders the game world and the perspective of the camera.
     */
    private Renderer renderer;
    /**
     * The minimap -- renders the minimap.
     */
    private Minimap minimap;
    /**
     * The HUD -- renders player health, etc.
     */
    private HUD hud;
    private StartMenu startMenu;
    private MultiplayerMenu mpMenu;
    private SettingsMenu settingsMenu;
    private ArrayList<Object> settingsObj;
    private GUIState gs = GUIState.START_MENU;
    
    public Display() {
        this.guis = new ArrayList<>();
        this.settingsObj = new ArrayList<>();
    }
    
    @Override
    public void render(PixelBuffer pix, int x, int y) {
        switch (GameEngine.getSingleton().guiState) {
            case IN_GAME:
                renderer.render();
                minimap.render();
                hud.render();
                
                final int w = pix.getWidth();
                pix.composite(renderer.getPixelBuffer(), x, y);
                
                PixelBuffer minimapBuffer = minimap.getPixelBuffer();
                pix.composite(minimapBuffer, x + w - minimapBuffer.getWidth(), y);
                
                PixelBuffer hudBuffer = hud.getPixelBuffer();
                pix.composite(hudBuffer, x,
                        y + renderer.getPixelBuffer().getHeight());
                
                ClientWorld world = (ClientWorld)GameEngine.getSingleton().getWorld();
                GameChat chat = world.getGameChat();
                GameFeed.getInstance().getFeed(chat);
                
                for (GUICanvas g : guis) {
                    g.render(pix, x, y);
                }
                
                GameFeed.getInstance().render(pix, x, y);
                
                this.clearDisplay();
                gs = GUIState.IN_GAME;
                break;
            
            case START_MENU:
                if (this.startMenu == null) {
                    this.startMenu = new StartMenu(pix.getHeight(), pix.getWidth());
                }
                this.startMenu.render(pix, x, y);
                this.clearDisplay();
                gs = GUIState.START_MENU;
                break;
            
            case MULTIPLAYER_MENU:
                if (this.mpMenu == null) {
                    this.mpMenu = new MultiplayerMenu(pix.getHeight(),
                            pix.getWidth());
                }
                this.mpMenu.render(pix, x, y);
                this.clearDisplay();
                gs = GUIState.MULTIPLAYER_MENU;
                break;
            
            case SETTINGS_MENU:
                if (this.settingsMenu == null) {
                    this.settingsMenu = new SettingsMenu(pix.getWidth(), pix.getHeight());
                }
                if (gs != GUIState.SETTINGS_MENU) {
                    this.settingsObj = this.settingsMenu.getObj();
                }
                this.settingsMenu.render(pix, x, y);
                this.clearDisplay();
                gs = GUIState.SETTINGS_MENU;
                break;
            
            case SETTINGS_MENU_APPLY:
                this.clearDisplay();
                GameEngine.getSingleton().guiState = GUIState.START_MENU;
                break;
            
            case SETTINGS_MENU_CANCEL:
                this.settingsMenu.setObj(this.settingsObj);
                this.clearDisplay();
                GameEngine.getSingleton().guiState = GUIState.START_MENU;
                break;
            
            default:
                this.clearDisplay();
                break;
        }
    }
    
    public void clearDisplay() {
        switch (GameEngine.getSingleton().guiState) {
            case IN_GAME:
                if (this.mpMenu != null) {
                    this.mpMenu = null;
                }
                if (this.startMenu != null) {
                    this.startMenu = null;
                }
                return;
            case START_MENU:
                if (this.mpMenu != null) {
                    this.mpMenu = null;
                }
                return;
            case MULTIPLAYER_MENU:
                if (this.startMenu != null) {
                    this.startMenu = null;
                }
                return;
            case SETTINGS_MENU:
                if (this.mpMenu != null) {
                    this.mpMenu = null;
                }
                if (this.startMenu != null) {
                    this.startMenu = null;
                }
            default:
                return;
        }
    }
    
    public void setRenderer(Renderer r) {
        this.renderer = r;
    }
    
    public void setMinimap(Minimap m) {
        this.minimap = m;
    }
    
    public void setHud(HUD hud) {
        this.hud = hud;
    }
    
    public void addGUICanvas(GUICanvas g) {
        guis.add(g);
    }
    
}
