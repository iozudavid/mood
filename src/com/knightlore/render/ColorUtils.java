package com.knightlore.render;

import java.awt.Color;

public class ColorUtils {
	
	// Defeat instatiation.
	private ColorUtils() {
	}
	
	public static int mixColor(int color1, int color2, double mix) {
		Color c1 = new Color(color1);
		Color c2 = new Color(color2);

		int r = (int) (c1.getRed() * (1 - mix) + c2.getRed() * mix);
		int g = (int) (c1.getGreen() * (1 - mix) + c2.getGreen() * mix);
		int b = (int) (c1.getBlue() * (1 - mix) + c2.getBlue() * mix);
		Color result = new Color(r, g, b);

		return result.getRGB();
	}

}
