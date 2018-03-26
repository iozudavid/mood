package com.knightlore.render.graphic.sprite;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.utils.Vector2D;

public class DirectionalSprite {
    
    public static final DirectionalSprite PISTOL_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.PISTOL_SPRITES);
    public static final DirectionalSprite SHOTGUN_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.SHOTGUN_SPRITES);
    public static final DirectionalSprite TURRET_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.TURRET_SPRITES);
    public static final DirectionalSprite CAMERA_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.CAMERA_SPRITES);
    public static final DirectionalSprite GRAVESTONE_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.GRAVESTONE_SPRITES);
    public static final DirectionalSprite SPEED_DIRECTION_SPRITE = new DirectionalSprite(
            GraphicSheet.SPEED_SPRITES);
    public static final DirectionalSprite HEALTHKIT_DIRECTIONAL_SPRITE = new DirectionalSprite(
            GraphicSheet.HEALTHKIT_SPRITES);
    private static final int ANGLES = 32;
    private final List<Graphic> angles;
    
    public DirectionalSprite(List<Graphic> angles) {
        this.angles = angles;
    }
    
    public DirectionalSprite(GraphicSheet sheet) {
        this.angles = new ArrayList<>(ANGLES);
        for (int i = 0; i < ANGLES; i++) {
            addGraphic(sheet.graphicAt(0, i));
        }
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
        int i = (int)(Math.floor(theta / c));
        
        i = i % angles.size();
        return i;
    }
    
    private void addGraphic(Graphic g) {
        angles.add(g);
    }
    
    public int getWidth() {
        return angles.get(0).getWidth();
    }
    
    public int getHeight() {
        return angles.get(0).getHeight();
    }
    
}
