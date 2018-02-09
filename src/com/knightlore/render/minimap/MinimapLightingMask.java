package com.knightlore.render.minimap;

import com.knightlore.render.ColorUtils;
import com.knightlore.render.Environment;
import com.knightlore.render.PixelBuffer;

public class MinimapLightingMask {

	private Environment env;

	public MinimapLightingMask(Environment env) {
		this.env = env;
	}

	public int adjustForLighting(PixelBuffer pix, int color, int xx, int yy) {
		final double DARKNESS_COEFFICIENT = 30000 * 2;
		double d = Math.pow(pix.getWidth() / 2 - xx, 2)
				+ Math.pow(pix.getHeight() / 2 - yy, 2);
		color = ColorUtils.mixColor(color, 0x000000,
				(env.getDarkness()) / DARKNESS_COEFFICIENT * d);
		return color;
	}

}
