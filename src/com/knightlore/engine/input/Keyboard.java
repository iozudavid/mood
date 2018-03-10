package com.knightlore.engine.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.knightlore.gui.GUICanvas;

public class Keyboard extends KeyAdapter {

    private boolean[] keys;

    public Keyboard() {
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
        keys[e.getKeyCode()] = false;
        // determine if this was left or right
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            GUICanvas.inputLeftArrow();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            GUICanvas.inputRightArrow();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    	char eChar = e.getKeyChar();
    	//vk_back_space not working here
    	if(eChar=='\b')
    		GUICanvas.deleteChar();
    	else
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
