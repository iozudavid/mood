package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.knightlore.render.ColorUtils;
import com.knightlore.render.PixelBuffer;

public class GuiUtils {
	
	public static int middleWidth(int screenWidth, int objWidth){
		int middle = screenWidth - objWidth;
		return middle/2;
	}
	
    public static int formatToRight(int screenWidth, int objWidth) {
        int toRight = screenWidth - objWidth;
        return toRight;
    }
	
	public static int calculateHeight(int screenHeight, float procent){
		double decimal = (double)procent/(double)100;
		return (int)(decimal*screenHeight);
	}
	
	public static int minX(ArrayList<GUIObject> g){
		if(g.size()==0)
			return 0;
		int min = g.get(0).getRectangle().x;
		for(GUIObject obj : g)
			min = Math.min(min, obj.getRectangle().x);
		return min;
	}
	
	public static int minY(ArrayList<GUIObject> g){
		if(g.size()==0)
			return 0;
		int min = g.get(0).getRectangle().y;
		for(GUIObject obj : g)
			min = Math.min(min, obj.getRectangle().y);
		return min;
	}
	
	public static Color makeTransparent(Color source, int alpha) {
		return new Color(source.getRed(), source.getGreen(), source.getBlue(), alpha);
	}
	
	public static Color makeChromaColor(Color source, double percent) {
		Color newColor;
		newColor = new Color(ColorUtils.mixColor(source.getRGB(), PixelBuffer.CHROMA_KEY, percent));
		return newColor;
    }
	
}
