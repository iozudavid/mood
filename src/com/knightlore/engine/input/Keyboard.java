package com.knightlore.engine.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameState;
import com.knightlore.gui.GUICanvas;

/**
 * The keyboard is the class responsible for detecting which keys are pressed at
 * a low-level. The keyboard should be attached as a KeyListener to the game
 * canvas so that it can monitor the state of the keyboard.
 * 
 * @author Joe Ellis
 *
 */
public class Keyboard extends KeyAdapter {

    /**
     * Keys are represented by their AWT keycodes. This array maps AWT keycodes
     * to a boolean value representing whether or not they are pressed. For
     * instance, if keys[KeyEvent.VK_W] == true then the key 'w' is pressed.
     */
    private boolean[] keys;

    public Keyboard() {
        // 0-255 encapsulates the range of keycodes we might want to use
        // sufficiently.
        keys = new boolean[256];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // defend against bad keys
        if (e.getKeyCode() < 0 || e.getKeyCode() > keys.length) {
            return;
        }
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // defend against bad keys
        if (e.getKeyCode() < 0 || e.getKeyCode() > keys.length) {
            return;
        }
    
        if(GameEngine.getSingleton().gameState==GameState.InGame && e.getKeyChar()=='q'){
        	GUICanvas.releaseQ();
        } 
        keys[e.getKeyCode()] = false;
        // determine if this was left or right
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            GUICanvas.inputLeftArrow();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            GUICanvas.inputRightArrow();
        }
    }
    
    public boolean isTyping(){
    	return GUICanvas.isTyping();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    	char eChar = e.getKeyChar();
    	//vk_back_space not working here
    	if(eChar=='\b')
    		GUICanvas.deleteChar();
    	else if(GameEngine.getSingleton().gameState==GameState.InGame && eChar=='t'){
    		GUICanvas.startMessageTeam(e.getKeyChar());
    	} else if(GameEngine.getSingleton().gameState==GameState.InGame && eChar=='y'){
    		GUICanvas.startMessageAll(e.getKeyChar());
    	} else if(GameEngine.getSingleton().gameState==GameState.InGame && eChar=='\n'){
    		GUICanvas.sendMessage(e.getKeyChar());
    	} else if(GameEngine.getSingleton().gameState==GameState.InGame && e.getKeyChar()==KeyEvent.VK_ESCAPE){
    		GUICanvas.escape();
    	} else if(GameEngine.getSingleton().gameState==GameState.InGame && eChar=='q'){
    		GUICanvas.pressQ();
    	} else
    		GUICanvas.inputChar(e.getKeyChar());
    }

    /**
     * Check whether a key is pressed given by its keycode.
     * 
     * @param kc
     *            the keycode (found in KeyEvent.VK_...)
     * @return true if the key is currently being pressed, false otherwise.
     */
    public boolean isPressed(int kc) {
        return keys[kc];
    }

}
