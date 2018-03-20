package com.knightlore.render.graphic.sprite;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.utils.Vector2D;

public class DirectionalSprite {

    public static final PlayerSprite PLAYER_DIRECTIONAL_SPRITE = new PlayerSprite(GraphicSheet.PLAYER_SPRITES);

    public static final ShotgunSprite SHOTGUN_DIRECTIONAL_SPRITE = new ShotgunSprite(GraphicSheet.SHOTGUN_SPRITES);

    public static final TurretSprite TURRET_DIRECTIONAL_SPRITE = new TurretSprite();

    public static final CameraSprite CAMERA_DIRECTIONAL_SPRITE = new CameraSprite(GraphicSheet.CAMERA_SPRITES);

    public static final GravestoneSprite GRAVESTONE_DIRECTIONAL_SPRITE = new GravestoneSprite(
            GraphicSheet.GRAVESTONE_SPRITES);

    private List<Graphic> angles;

    public DirectionalSprite() {
        this(new ArrayList<>());
    }

    public DirectionalSprite(ArrayList<Graphic> angles) {
        this.angles = angles;
    }

    public Graphic getCurrentGraphic(Vector2D myPosition, Vector2D myDirection, Vector2D viewPosition) {
        int i = getIndex(myPosition, myDirection, viewPosition);
        return angles.get(i);
    }

    private int getIndex(Vector2D myPosition, Vector2D myDirection, Vector2D viewPosition) {

        Vector2D displacement = Vector2D.sub(myPosition, viewPosition);

        double dot = myDirection.dot(displacement);
        double det = myDirection.getX() * displacement.getY() - displacement.getX() * myDirection.getY();
        double theta = Math.atan2(det, dot);
        theta += Math.PI;

        double c = (2 * Math.PI) / angles.size();
        int i = (int) (Math.floor(theta / c));

        i = i % angles.size();
        return i;
    }

    public void addGraphic(Graphic g) {
        angles.add(g);
    }

    public List<Graphic> getAngles() {
        return angles;
    }

    public int getWidth() {
        return angles.get(0).getWidth();
    }

    public int getHeight() {
        return angles.get(0).getHeight();
    }

}
