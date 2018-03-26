package com.knightlore.game.entity;

import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Team;
import com.knightlore.game.area.Map;
import com.knightlore.game.buff.Buff;
import com.knightlore.game.buff.BuffType;
import com.knightlore.game.tile.Tile;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.render.minimap.IMinimapObject;
import com.knightlore.utils.Vector2D;

/**
 * An entity is any physical object that exists in the game world that is not a
 * tile.
 * 
 * @authors All
 *
 */
public abstract class Entity extends NetworkObject implements IMinimapObject, TickListener {
    
    /**
     * The speed at which the entity can move when calling moveForward() and
     * moveBackward().
     */
    protected double moveSpeed = .04;
    
    /**
     * The speed at which the entity can move when calling moveLeft() and
     * moveRight().
     */
    protected double strafeSpeed = .01;
    
    /**
     * The speed at which the entity can rotate when calling rotateClockwise()
     * and rotateAnticlockwise().
     */
    protected double rotationSpeed = .025;
    
    /**
     * A value by which incoming damage is multiplied.
     */
    protected double damageTakenModifier = 1;
    
    /**
     * A list of active buffs possessed by the entity.
     */
    private final ArrayList<Buff> buffList = new ArrayList<>();
    
    /**
     * Rate at which buffs are updated.
     */
    private static final double BUFF_TICK_RATE = GameEngine.UPDATES_PER_SECOND / 16;
    
    // this constant will decide
    // how smooth will be rendered other entities
    private static final double SMOOTHINESS = 0.1D;
    
    /**
     * The map which the entity exists in. This is required for collision
     * detection.
     */
    private final Map map;
    
    /**
     * The size of the bounding rectangle of the entity.
     */
    protected double size;
    
    protected Vector2D direction = Vector2D.ONE;
    protected Vector2D plane = Vector2D.ONE;
    
    // cannot have invalid values
    // anyone can set a team and get a team
    public Team team = Team.NONE;
    
    /**
     * Used for rendering exclusively. A higher zOffset means that the entities
     * are rendered more closely to the floor.
     */
    protected int zOffset;
    private boolean creationCall;
    private boolean settingCall;
    
    private String name = "entity";
    
    private final BlockingQueue<ByteBuffer> systemMessages;
    
    // Allow you to create an entity with a specified UUID. Useful for creating
    // "synchronised" objects on the client-side.
    protected Entity(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, position);
        this.size = size;
        this.direction = direction;
        this.plane = direction.perpendicular();
        this.zOffset = 0;
        map = GameEngine.getSingleton().getWorld().getMap();
        
        // tick listener for buffs
        GameEngine.ticker.addTickListener(this);
        
        this.creationCall = false;
        this.settingCall = false;
        this.systemMessages = new LinkedBlockingQueue<>();
    }
    
    /**
     * Creates an Entity with random UUID
     * 
     * @param size - the collision size of the entity
     * @param position
     * @param direction
     */
    protected Entity(double size, Vector2D position, Vector2D direction) {
        this(UUID.randomUUID(), size, position, direction);
    }
    
    /**
     * Entity collision size defaults to 1
     * 
     * @param uuid
     * @param position
     * @param direction
     */
    protected Entity(UUID uuid, Vector2D position, Vector2D direction) {
        this(uuid, 1, position, direction);
    }
    
    /**
     * This is called if this particular entity is the camera subject.
     * Subclasses are free to override this. By default, it does nothing.
     * 
     * @param pix
     *            the pixelbuffer to render to.
     * @param x
     *            the x-position of where to start rendering.
     * @param y
     *            the y-positon of where to start rendering.
     * @param distanceTraveled
     *            the distance that the entity has travelled since the last
     *            update. Used to calculate inertia.
     */
    public void render(PixelBuffer pix, int x, int y, double distanceTraveled) {
        /* ONLY CALLED IF THIS ENTITY IS THE CAMERA SUBJECT */
    }
    
    /**
     * Called when a player collides with this entity. Subclasses can override
     * this to damage the player, increase their health, etc.
     * 
     * @param player
     *            the player that intersects with this entity.
     */
    public abstract void onCollide(Player player);
    
    /**
     * Periodically calls the onEntered method of the tile corresponding to the
     * player's current location.
     */
    @Override
    public void onUpdate() {
        // Tell the tile that we're currently standing on that we've entered it.
        Map map = GameEngine.getSingleton().getWorld().getMap();
        Tile t = map.getTile((int) position.getX(), (int) position.getY());
        t.onEntered(this);
    }
    
    /**
     * Given the position of the player, returns the appropriate directional
     * sprite graphic.
     * 
     * @param playerPos
     *            the position of the player (from where are we looking?)
     * @return the correct directional sprite graphic.
     */
    public Graphic getGraphic(Vector2D playerPos) {
        return getDirectionalSprite().getCurrentGraphic(position, direction, playerPos);
    }
    
    /**
     * Gets the current directional sprite of this entity.
     * 
     * @return the directional sprite for this entity.
     */
    public abstract DirectionalSprite getDirectionalSprite();
    
    public double getSize() {
        return size;
    }
    
    public Vector2D getDirection() {
        return direction;
    }
    
    /**
     * Gets an AWT.Rectangle2D.Double that bounds this entity.
     * 
     * @return an AWT.Rectangle2D.Double that bounds this entity.
     */
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(getxPos(), getyPos(), size, size);
    }
    
    public double getxDir() {
        return getDirection().getX();
    }
    
    public double getyDir() {
        return getDirection().getY();
    }
    
    public void setxDir(double xDir) {
        direction = new Vector2D(xDir, direction.getY());
        plane = direction.perpendicular();
    }
    
    public void setyDir(double yDir) {
        direction = new Vector2D(direction.getX(), yDir);
        plane = direction.perpendicular();
    }
    
    public Vector2D getPlane() {
        return plane;
    }
    
    public int getzOffset() {
        return zOffset;
    }
    
    public void respawn(Vector2D spawnPos) {
        this.position = spawnPos;
    }
    
    /**
     * Move this entity forward, accounting for collisions.
     */
    protected synchronized void moveForward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * moveSpeed));
        xPos += xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos += yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }
    
    /**
     * Move this entity backward, accounting for collisions.
     */
    protected synchronized void moveBackward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * moveSpeed));
        xPos -= xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos -= yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }
    
    /**
     * Move this entity left, accounting for collisions.
     */
    protected synchronized void strafeLeft() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * strafeSpeed));
        xPos -= yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos -= -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }
    
    /**
     * Move this entity right, accounting for collisions.
     */
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
     * Move the player irrespective of the direction they're facing
     */
    public synchronized void absoluteMove(Vector2D absDir, double distance) {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = absDir.getX(), yDir = absDir.getY();
        Tile xTile = map.getTile((int) (xPos + xDir * distance), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + yDir * distance));
        xPos += xDir * distance * (1 - xTile.getSolidity());
        yPos += yDir * distance * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }
    
    /**
     * Rotation is simply multiplication by the rotation matrix. We take the
     * position and plane vectors, then multiply them by the rotation matrix
     * (whose parameter is ROTATION_SPEED). This lets us rotate.
     */
    protected synchronized void rotateAntiClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(rotationSpeed) - yDir * Math.sin(rotationSpeed);
        yDir = oldxDir * Math.sin(rotationSpeed) + yDir * Math.cos(rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = direction.perpendicular();
    }
    
    /**
     * Same as rotating left but clockwise this time.
     */
    protected synchronized void rotateClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(-rotationSpeed) - yDir * Math.sin(-rotationSpeed);
        yDir = oldxDir * Math.sin(-rotationSpeed) + yDir * Math.cos(-rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = direction.perpendicular();
    }
    
    @Override
    public synchronized ByteBuffer serialize() {
        ByteBuffer buf = newByteBuffer("deserialize");
        buf.putDouble(this.moveSpeed);
        buf.putDouble(this.strafeSpeed);
        buf.putDouble(this.rotationSpeed);
        buf.putDouble(size);
        buf.putDouble(position.getX());
        buf.putDouble(position.getY());
        buf.putDouble(direction.getX());
        buf.putDouble(direction.getY());
        buf.putDouble(plane.getX());
        buf.putDouble(plane.getY());
        NetworkUtils.putStringIntoBuf(buf, name);
        // buf.putInt(zOffset);
        return buf;
    }
    
    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        // interpolation only on client side
        if (!this.creationCall || !this.settingCall
                || GameSettings.isServer()) {
            this.moveSpeed = buf.getDouble();
            this.strafeSpeed = buf.getDouble();
            this.rotationSpeed = buf.getDouble();
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
            
            // before starting interpolation
            // we need to wait for setting entity up...
            if (!this.creationCall) {
                this.creationCall = true;
            } else {
                this.settingCall = true;
            }
        } else {
            this.moveSpeed = buf.getDouble();
            this.strafeSpeed = buf.getDouble();
            this.rotationSpeed = buf.getDouble();
            size = buf.getDouble();
            double posX = buf.getDouble();
            double posY = buf.getDouble();
            if (this.posT1 == null) {
                this.posT1 = new Vector2D(posX, posY);
            } else if (this.posT2 == null) {
                this.posT2 = new Vector2D(posX, posY);
                this.position = interpolate(this.position, this.posT1);
            } else {
                Vector2D aux = this.posT1;
                this.posT1 = this.posT2;
                this.posT2 = new Vector2D(posX, posY);
                this.position = interpolate(this.position, aux);
            }
            double dirX = buf.getDouble();
            double dirY = buf.getDouble();
            if (this.dirT1 == null) {
                this.dirT1 = new Vector2D(dirX, dirY);
            } else if (this.dirT2 == null) {
                this.dirT2 = new Vector2D(dirX, dirY);
                this.direction = interpolate(this.direction, this.dirT1);
            } else {
                Vector2D aux = this.dirT1;
                this.dirT1 = this.dirT2;
                this.dirT2 = new Vector2D(dirX, dirY);
                this.direction = interpolate(this.direction, aux);
            }
            double planeX = buf.getDouble();
            double planeY = buf.getDouble();
            plane = new Vector2D(planeX, planeY);
            // zOffset = buf.getInt();
            this.creationCall = true;
        }
        name = NetworkUtils.getStringFromBuf(buf);
    }
    
    private Vector2D interpolate(Vector2D local, Vector2D remote) {
        double finalX;
        double finalY;
        double difX = remote.getX() - local.getX();
        if (Math.abs(difX) < this.treshold) {
            finalX = remote.getX();
        } else {
            finalX = local.getX() + difX * SMOOTHINESS;
        }
        double difY = remote.getY() - local.getY();
        if (Math.abs(difY) < this.treshold) {
            finalY = remote.getY();
        } else {
            finalY = local.getY() + difY * SMOOTHINESS;
        }
        return new Vector2D(finalX, finalY);
    }
    
    @Override
    public double getDrawSize() {
        return 2 * size;
    }
    
    public void setSize(double s) {
        this.size = s;
    }
    
    /**
     * Called to notify the entity when it should take damage. Does nothing by
     * default
     * 
     * @param damage
     *            - how much damage to take
     * @param inflictor
     *            - what caused this damage
     */
    public void takeDamage(int damage, Entity inflictor) {
        // DO NOTHING
    }
    
    public void setMoveSpeed(double speed) {
        moveSpeed = speed;
    }
    
    public double getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setRotateSpeed(double speed) {
        rotationSpeed = speed;
    }
    
    public double getRotateSpeed() {
        return rotationSpeed;
    }
    
    public void setStrafeSpeed(double speed) {
        strafeSpeed = speed;
    }
    
    public double getStrafeSpeed() {
        return strafeSpeed;
    }
    
    public double getDamageTakenModifier() {
        return damageTakenModifier;
    }
    
    public void setDamageTakenModifier(double d) {
        damageTakenModifier = d;
    }
    
    /**
     * Removes all buffs of the specified BuffType by calling it's setDone
     * method.
     * 
     * @param bt
     *            - the buff type to remove
     */
    public synchronized void removeBuff(BuffType bt) {
        for (Buff b : buffList) {
            if (b.getType() == bt) {
                b.setDone(true);
            }
        }
    }
    
    /**
     * Append the given buff to the buff list, and call its onApply() method.
     * 
     * @param buff
     */
    private synchronized void addBuff(Buff buff) {
        buffList.add(buff);
        buff.onApply();
    }
    
    /**
     * Restarts the current buff if its type is present in the entity's buff
     * list, otherwise adds it to the buff list.
     * 
     * @param rbuff
     */
    public synchronized void resetBuff(Buff rbuff) {
        BuffType bt = rbuff.getType();
        for (Buff buff : buffList) {
            if (buff.getType() == bt) {
                buff.reset();
                return; // IMPORTANT WE RETURN
            }
        }
        addBuff(rbuff);
    }
    
    /**
     * Sets all buffs to done, ensuring they will be removed from the list on
     * the next onTick() call.
     */
    public synchronized void removeAllBuffs() {
        for (Buff aBuffList : buffList) {
            aBuffList.setDone(true);
        }
    }
    
    /**
     * Iterates over the buff list, removing buffs or continuing their execution
     * as appropriate.
     */
    @Override
    public synchronized void onTick() {
        
        for (Iterator<Buff> iter = buffList.iterator(); iter.hasNext();) {
            Buff buff = iter.next();
            if (buff.isDone()) {
                buff.onRemove();
                iter.remove();
            } else {
                buff.loop();
            }
        }
    }
    
    /**
     * Returns the rate at which buffs are checked and executed
     * 
     * @return BUFF_TICK_RATE
     */
    public static double getBuffTickRate() {
        return BUFF_TICK_RATE;
    }
    
    @Override
    public long interval() {
        // every sixteenth second
        return (long) GameEngine.UPDATES_PER_SECOND / 16;
    }
    
    /**
     * Prints the supplied <code> message </code> to the chat and sends to
     * everyone with "System" as the sender.
     * 
     * @param message
     */
    public void sendSystemMessage(String message) {
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.systemMessages.offer(bf);
    }
    
    /**
     * Sends a death message with the specified name, to all players. This
     * message will appear in the chat.
     * 
     * @param name
     *            - name to display
     * @param inflictor
     *            - what caused this death
     */
    protected void sendSystemMessage(String name, Entity inflictor) {
        String message;
        if (inflictor == null) {
            message = "System: " + name + " was killed by natural causes";
        } else {
            message = "System : " + name + " was killed by " + inflictor.getName();
        }
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.systemMessages.offer(bf);
    }
    
    /**
     * Called by the server when synchronizing the state to send system messages.
     * 
     * @returns a byte buffer containing a single pending system chat message.
     */
    public Optional<ByteBuffer> getSystemMessages() {
        if (this.systemMessages.isEmpty()) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(this.systemMessages.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Whether or not to render this entities name above it.
     */
    public boolean renderName() {
        return false;
    }
    
}
