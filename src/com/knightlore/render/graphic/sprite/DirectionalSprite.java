package com.knightlore.render.graphic.sprite;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Vector2D;

public class DirectionalSprite {

	public static final ShotgunSprite SHOTGUN_DIRECTIONAL_SPRITE = new ShotgunSprite();

	private List<Graphic> angles;

	public DirectionalSprite() {
		this(new ArrayList<Graphic>());
	}

	public DirectionalSprite(ArrayList<Graphic> angles) {
		this.angles = angles;
	}

	public Graphic getCurrentGraphic(Vector2D myPosition, Vector2D myDirection, Vector2D viewPosition) {

		Vector2D displacement = Vector2D.sub(myPosition, viewPosition);

		double dot = myDirection.dot(displacement);
		double det = myDirection.getX() * displacement.getY() - displacement.getX() * myDirection.getY();
		double theta = Math.atan2(det, dot);
		theta += Math.PI;

		double c = (2 * Math.PI) / angles.size();
		int i = (int) (Math.floor(theta / c));

		return angles.get(i);
	}

	public void addGraphic(Graphic g) {
		angles.add(g);
	}

}
