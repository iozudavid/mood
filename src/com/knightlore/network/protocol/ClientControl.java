package com.knightlore.network.protocol;

import java.io.IOException;

import com.sun.glass.events.KeyEvent;

public enum ClientControl {
	FORWARD,
	LEFT,
	BACKWARD,
	RIGHT,
	ROTATE_CLOCKWISE,
	ROTATE_ANTI_CLOCKWISE,
	SHOOT;
	
	public static int getKeyCode(ClientControl k) throws IOException{
		switch(k){
			//at this moment only this keys are provided
			case FORWARD : return KeyEvent.VK_W;
			case LEFT : return KeyEvent.VK_A;
			case BACKWARD : return KeyEvent.VK_S;
			case RIGHT : return KeyEvent.VK_D;
			default: throw new IOException();
		}
					
	}
	
}
