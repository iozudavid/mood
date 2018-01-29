package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;

public class PerspectiveRenderItem {

	public double opacity;
	public int drawStart;
	public int drawEnd;
	public int lineHeight;
	
	public Graphic texture;

	public int texX;
	public double distanceToWall;
	public int xx;

	public PerspectiveRenderItem(double opacity, int drawStart, int drawEnd, int lineHeight, Graphic texture, int texX,
			double distanceToWall, int xx) {
		this.opacity = opacity;
		this.drawStart = drawStart;
		this.drawEnd = drawEnd;
		this.lineHeight = lineHeight;
		this.texture = texture;
		this.texX = texX;
		this.distanceToWall = distanceToWall;
		this.xx = xx;
	}

}
