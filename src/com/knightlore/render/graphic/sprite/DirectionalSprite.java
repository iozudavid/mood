package com.knightlore.render.graphic.sprite;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.utils.Vector2D;

public class DirectionalSprite {

    public static final PlayerSprite PLAYER_DIRECTIONAL_SPRITE = new PlayerSprite(GraphicSheet.PLAYER_SPRITES);
    public static final PlayerSprite RED_PLAYER_DIRECTIONAL_SPRITE = new PlayerSprite(GraphicSheet.RED_PLAYER_SPRITES);
    public static final PlayerSprite BLUE_PLAYER_DIRECTIONAL_SPRITE = new PlayerSprite(
            GraphicSheet.BLUE_PLAYER_SPRITES);

    public static final ShotgunSprite SHOTGUN_DIRECTIONAL_SPRITE = new ShotgunSprite(GraphicSheet.SHOTGUN_SPRITES);
    public static final ShotgunSprite RED_SHOTGUN_DIRECTIONAL_SPRITE = new ShotgunSprite(
            GraphicSheet.RED_SHOTGUN_SPRITES);
    public static final ShotgunSprite BLUE_SHOTGUN_DIRECTIONAL_SPRITE = new ShotgunSprite(
            GraphicSheet.BLUE_SHOTGUN_SPRITES);
    
    public static final TurretSprite TURRET_DIRECTIONAL_SPRITE = new TurretSprite();
    
    public static final CameraSprite CAMERA_DIRECTIONAL_SPRITE = new CameraSprite(GraphicSheet.CAMERA_SPRITES);

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

//        // FIXME
//        // quick fix
//        // must be REMOVED
//        if (i >= angles.size())
//            return angles.get(angles.size() - 1);
        // for some reason, without this line turrets try to access sprite 32
        i = i%angles.size();
        
        return angles.get(i);
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
