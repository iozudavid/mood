package com.knightlore.gui;

public class GuiUtils {
	
	public static int middleWidth(int screenWidth, int objWidth){
		int middle = screenWidth - objWidth;
		return middle/2;
	}
	
	public static int calculateHeight(int screenHeight, int procent){
		double decimal = (double)procent/(double)100;
		return (int)(decimal*screenHeight);
	}
}
