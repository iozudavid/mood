package com.knightlore.render;

import java.awt.Color;

public enum Environment {
	DARK_OUTDOORS(15) {
		@Override
		public void renderEnvironment(PixelBuffer pix) {
			for (int yy = pix.getHeight() / 2; yy < pix.getHeight(); yy += 1) {
				for (int xx = 0; xx < pix.getWidth(); xx++) {
					int n = Math.max(0, Math.min(yy * yy / 5000, 255));
					Color c = new Color(n, n, n);
					pix.fillRect(c.getRGB(), xx, yy, 1, 1);
					pix.fillRect(c.getRGB(), xx, -pix.getHeight() / 2 + yy, 1, 1);
				}
			}
		}

		@Override
		public int getMinimapBaseColor() {
			return 0x33333;
		}
	},
	LIGHT_OUTDOORS(3) {
		@Override
		public void renderEnvironment(PixelBuffer pix) {

			final int skyClose = 0x7ec0ee, skyFar = 0xffffff;
			final int grassClose = 0x074A00, grassFar = 0xffffff;

			for (int yy = 0; yy < pix.getHeight() / 2; yy += 1) {
				int sky = ColorUtils.mixColor(skyClose, skyFar, yy / (double) (pix.getHeight()) / 2);
				int grass = ColorUtils.mixColor(grassClose, grassFar, yy / (double) (pix.getHeight()) / 2);
				for (int xx = 0; xx < pix.getWidth(); xx++) {
					pix.fillRect(sky, xx, yy, 1, 1);
					pix.fillRect(grass, xx, pix.getHeight() - yy, 1, 1);
				}
			}
		}

		@Override
		public int getMinimapBaseColor() {
			return 0x074A00;
		}
	},
	DUNGEON(20) {
		@Override
		public void renderEnvironment(PixelBuffer pix) {
			pix.fillRect(0x000000, 0, 0, pix.getWidth(), pix.getHeight());
		}

		@Override
		public int getMinimapBaseColor() {
			return 0x000000;
		}
	};

	private final int darkness;

	Environment(int darkness) {
		this.darkness = darkness;
	}

	public abstract void renderEnvironment(PixelBuffer pix);

	public abstract int getMinimapBaseColor();

	public int getDarkness() {
		return darkness;
	}

}
