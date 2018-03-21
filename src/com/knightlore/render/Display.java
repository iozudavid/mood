package com.knightlore.render;

import com.knightlore.engine.GameEngine;
import java.util.ArrayList;
import java.util.List;

import com.knightlore.gui.GUICanvas;
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
 *
 */
public class Display implements IRenderable {

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
    private GameChat chat;
    private StartMenu startMenu;
    private MultiplayerMenu mpMenu;
    private SettingsMenu settingsMenu;
    private List<GUICanvas> guis;

    public Display(){}
    
	public Display(Renderer renderer, Minimap minimap, HUD hud, GameChat chat, StartMenu startMenu,
			MultiplayerMenu mpMenu) {
		this.renderer = renderer;
		this.minimap = minimap;
		this.hud = hud;
		this.chat = chat;
		this.startMenu = startMenu;
		this.mpMenu = mpMenu;
	}

    @Override
    public void render(PixelBuffer pix, int x, int y) {
		switch (GameEngine.getSingleton().gameState) {
		case InGame:
			renderer.render();
			minimap.render();
			hud.render();
			chat.render();

			final int w = pix.getWidth(), h = pix.getHeight();
			pix.composite(renderer.getPixelBuffer(), x, y);

			PixelBuffer minimapBuffer = minimap.getPixelBuffer();
			pix.composite(minimapBuffer, x + w - minimapBuffer.getWidth(), y);

			PixelBuffer hudBuffer = hud.getPixelBuffer();
			pix.composite(hudBuffer, x, y + renderer.getPixelBuffer().getHeight());

			GameFeed.getInstance().getFeed(this.chat);

			PixelBuffer chatBuffer = chat.getPixelBuffer();
			pix.composite(chatBuffer, x, y);
			
			GameFeed.getInstance().render(pix, x, y);

			this.clearDisplay();
			break;
			
		case StartMenu:
			if(this.startMenu==null)
				this.startMenu=new StartMenu(pix.getHeight(), pix.getWidth());
			this.startMenu.render(pix, x, y);
			this.clearDisplay();
			break;
			
		case MultiplayerMenu:
			if(this.mpMenu==null)
				this.mpMenu=new MultiplayerMenu(pix.getHeight(), pix.getWidth());
			this.mpMenu.render(pix, x, y);
			this.clearDisplay();
			break;
			
		case SettingsMenu:
		    if(this.settingsMenu==null)
		        this.settingsMenu=new SettingsMenu(pix.getWidth(), pix.getHeight());
		    this.settingsMenu.render(pix, x, y);
		    this.clearDisplay();
			
		default:
			this.clearDisplay();
			break;
		}
    }
    
    public void clearDisplay(){
    	switch (GameEngine.getSingleton().gameState) {
		case InGame:
			if(this.mpMenu!=null)
				this.mpMenu=null;
			if(this.startMenu!=null)
				this.startMenu=null;
			return;
		case StartMenu:
			if(this.mpMenu!=null)
				this.mpMenu=null;
			return;
		case MultiplayerMenu:
			if(this.startMenu!=null)
				this.startMenu=null;
			return;
		case SettingsMenu:
		    if(this.mpMenu!=null)
                this.mpMenu=null;
            if(this.startMenu!=null)
                this.startMenu=null;
		default:
			return;
    	}
    }
    
    public void setMinimap(Minimap m){
    	this.minimap=m;
    }
    
    public void setRenderer(Renderer r){
    	this.renderer = r;
    }
    
    public void setHud(HUD hud){
    	this.hud=hud;
    }
    
    public void setGameChat(GameChat gc){
    	this.chat = gc;
    }
    
    public void setStartMenu(StartMenu sm){
    	this.startMenu = sm;
    }
    
    public void setMultiplayerMenu(MultiplayerMenu mp){
    	this.mpMenu = mp;


    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Minimap getMinimap() {
        return minimap;
    }

    public HUD getHud() {
        return hud;
    }
    
    public GameChat getChat(){
    	return this.chat;
    }
    
    public void addGUICanvas(GUICanvas g) {
        guis.add(g);
    }
    
    public void removeGUICanvas(GUICanvas g) {
        guis.remove(g);
    }

}
