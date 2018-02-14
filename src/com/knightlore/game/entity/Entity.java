package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.IMinimapObject;
import com.knightlore.utils.Vector2D;

public abstract class Entity extends NetworkObject implements IMinimapObject {

    protected double moveSpeed = .040;
    protected double strafeSpeed = .01;
    protected double rotationSpeed = .025;

    private Map map;

    protected DirectionalSprite dSprite;
    protected double size;
    protected Vector2D direction;
    protected Vector2D plane;

    protected int zOffset;
    
    // Allow you to create an entity with a specified UUID. Useful for creating "synchronised" objects on the client-side.
    protected Entity(UUID uuid, Map map, double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
        super(uuid, position);
        this.map = map;
        this.size = size;
        this.dSprite = dSprite;
        this.direction = direction;
        this.plane = direction.perpendicular();
        this.zOffset = 0;
    }

    // Use a random UUID.
    protected Entity(Map map, double size, DirectionalSprite dSprite, Vector2D position, Vector2D direction) {
        this(UUID.randomUUID(), map, size, dSprite, position, direction);
    }

    public Graphic getGraphic(Vector2D playerPos) {
        return dSprite.getCurrentGraphic(position, direction, playerPos);
    }

    public double getSize() {
        return size;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public double getxDir() {
        return getDirection().getX();
    }

    public double getyDir() {
        return getDirection().getY();
    }

    public void setxDir(double xDir) {
        direction = new Vector2D(xDir, direction.getY());
    }

    public void setyDir(double yDir) {
        direction = new Vector2D(direction.getX(), yDir);
    }

    public Vector2D getPlane() {
        return plane;
    }

    public double getxPlane() {
        return getPlane().getX();
    }

    public double getyPlane() {
        return getPlane().getY();
    }

    public void setxPlane(double xPlane) {
        plane = new Vector2D(xPlane, plane.getY());
    }

    public void setyPlane(double yPlane) {
        plane = new Vector2D(plane.getX(), yPlane);
    }

    public int getzOffset() {
        return zOffset;
    }

    public synchronized void moveForward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * moveSpeed));
        xPos += xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos += yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    public synchronized void moveBackward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * moveSpeed));
        xPos -= xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos -= yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    public synchronized void strafeLeft() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * strafeSpeed));
        xPos -= yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos -= -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    public synchronized void strafeRight() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + -xDir * strafeSpeed));
        xPos += yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos += -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    /**
     * Rotation is simply multiplication by the rotation matrix. We take the
     * position and plane vectors, then multiply them by the rotation matrix
     * (whose parameter is ROTATION_SPEED). This lets us rotate.
     */
    public synchronized void rotateAntiClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double xPlane = plane.getX(), yPlane = plane.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(rotationSpeed) - yDir * Math.sin(rotationSpeed);
        yDir = oldxDir * Math.sin(rotationSpeed) + yDir * Math.cos(rotationSpeed);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(rotationSpeed) - yPlane * Math.sin(rotationSpeed);
        yPlane = oldxPlane * Math.sin(rotationSpeed) + yPlane * Math.cos(rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = new Vector2D(xPlane, yPlane);
    }

    /**
     * Same as rotating left but clockwise this time.
     */
    public synchronized void rotateClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double xPlane = plane.getX(), yPlane = plane.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(-rotationSpeed) - yDir * Math.sin(-rotationSpeed);
        yDir = oldxDir * Math.sin(-rotationSpeed) + yDir * Math.cos(-rotationSpeed);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(-rotationSpeed) - yPlane * Math.sin(-rotationSpeed);
        yPlane = oldxPlane * Math.sin(-rotationSpeed) + yPlane * Math.cos(-rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = new Vector2D(xPlane, yPlane);
    }
    
    @Override
    public ByteBuffer serialize() {
        // TODO: serialise objects as well as primitives
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putDouble(size);
        buf.putDouble(direction.getX());
        buf.putDouble(direction.getY());
        buf.putDouble(plane.getX());
        buf.putDouble(plane.getY());
        buf.putInt(zOffset);
        return buf;
    }
    
    @Override
    public void deserialize(ByteBuffer buf) {
        size = buf.getDouble();
        double dX = buf.getDouble();
        double dY = buf.getDouble();
        direction = new Vector2D(dX, dY);
        double pX = buf.getDouble();
        double pY = buf.getDouble();
        plane = new Vector2D(pX, pY);
        zOffset = buf.getInt();
    }
}
