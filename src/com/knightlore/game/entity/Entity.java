package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
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

    protected double size;
    // Initialise these with vectors to avoid null pointers before
    // deserialisation occurs.
    protected Vector2D direction = Vector2D.ONE;
    protected Vector2D plane = Vector2D.ONE;

    protected int zOffset;

    // Allow you to create an entity with a specified UUID. Useful for creating
    // "synchronised" objects on the client-side.
    protected Entity(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, position);
        this.size = size;
        this.direction = direction;
        this.plane = direction.perpendicular();
        this.zOffset = 0;
        map = GameEngine.getSingleton().getWorld().getMap();
    }

    // Use a random UUID.
    protected Entity(double size, Vector2D position, Vector2D direction) {
        this(UUID.randomUUID(), size, position, direction);
    }

    public Graphic getGraphic(Vector2D playerPos) {
        return getDirectionalSprite().getCurrentGraphic(position, direction, playerPos);
    }

    public abstract DirectionalSprite getDirectionalSprite();

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

    protected synchronized void moveForward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * moveSpeed));
        xPos += xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos += yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void moveBackward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * moveSpeed));
        xPos -= xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos -= yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void strafeLeft() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * strafeSpeed));
        xPos -= yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos -= -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void strafeRight() {
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
    protected synchronized void rotateAntiClockwise() {
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
    protected synchronized void rotateClockwise() {
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
    public synchronized ByteBuffer serialize() {
        // TODO: serialise objects as well as primitives
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putDouble(size);
        buf.putDouble(position.getX());
        buf.putDouble(position.getY());
        buf.putDouble(direction.getX());
        buf.putDouble(direction.getY());
        buf.putDouble(plane.getX());
        buf.putDouble(plane.getY());
        // buf.putInt(zOffset);
        return buf;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        size = buf.getDouble();
        double posX = buf.getDouble();
        double posY = buf.getDouble();
        position = new Vector2D(posX, posY);
        double dirX = buf.getDouble();
        double dirY = buf.getDouble();
        direction = new Vector2D(dirX, dirY);
        double planeX = buf.getDouble();
        double planeY = buf.getDouble();
        plane = new Vector2D(planeX, planeY);
        // zOffset = buf.getInt();
    }
}
