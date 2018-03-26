package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Utils used to render game panels.
 * @author David Iozu
 *
 */
public class GuiUtils {
	
    /**
     * 
     * @param screenWidth
     *            - width of the screen
     * @param objWidth
     *            - width of the given object
     * @return the X Position to start render in order to allign the object on
     *         the middle of the width screen
     */
	public static int middleWidth(int screenWidth, int objWidth){
		int middle = screenWidth - objWidth;
		return middle/2;
	}
	
    /**
     * 
     * @param screenWidth
     *            - width of the screen
     * @param objWidth
     *            - width of the given object
     * @return the X Position to start render in order to allign the object on
     *         the right of the width screen to finish rendering it at the end
     *         of width screen
     */
    public static int formatToRight(int screenWidth, int objWidth) {
		return screenWidth - objWidth;
    }
	
    /**
     * 
     * @param screenHeight
     *            - height of the screen
     * @param procent
     *            - the percentage of the screen we want to leave before the
     *            object
     * @return the Y Position to start render in order to leave the given
     *         percentage before this object
     */
	public static int calculateHeight(int screenHeight, float procent){
		double decimal = (double)procent/(double)100;
		return (int)(decimal*screenHeight);
	}
	
	/**
	 * 
	 * @param g - list of GUIObjects we want to search on
	 * @return the X minimum position found in the list
	 */
	public static int minX(ArrayList<GUIObject> g){
		if(g.isEmpty()) {
			return 0;
		}
		int min = g.get(0).getRectangle().x;
		for(GUIObject obj : g) {
			min = Math.min(min, obj.getRectangle().x);
		}
		return min;
	}
	
	/**
	 * 
	 * @param g - list of GUIObjects we want to search on
	 * @return the Y minimum position found in the list
	 */
	public static int minY(ArrayList<GUIObject> g){
		if(g.isEmpty()) {
			return 0;
		}
		int min = g.get(0).getRectangle().y;
		for(GUIObject obj : g) {
			min = Math.min(min, obj.getRectangle().y);
		}
		return min;
	}
	
    /**
     * 
     * @param source
     *            - color which we want to apply transparency
     * @param alpha
     *            - how transparent we want to make it
     * @return the same color with given transparency applied
     */
	public static Color makeTransparent(Color source, int alpha) {
		return new Color(source.getRed(), source.getGreen(), source.getBlue(), alpha);
	}
	
}
