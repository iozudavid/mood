package com.knightlore.game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.ai.InputModule;
import com.knightlore.ai.RemoteInput;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.GameFeed;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity {

    public int score = 0;

    private final int MAX_HEALTH = 100;
    private int currentHealth = MAX_HEALTH;
    private Weapon currentWeapon;

    private boolean hasShot;
    private Vector2D prevPos, prevDir;
    private int inertiaX = 0, inertiaY = 0;

    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientController, Runnable> ACTION_MAPPINGS = new HashMap<>();
    private java.util.Map<ClientController, Byte> inputState = new HashMap<>();
    private InputModule inputModule = null;
    // private volatile boolean finished = false;
    private BlockingQueue<ByteBuffer> teamMessagesToSend;
    private BlockingQueue<ByteBuffer> allMessagesToSend;


	private double timeToSend = 0;
	private boolean respawn = false;
    
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("Player build, state size: " + state.remaining());
        NetworkObject obj = new Player(uuid, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public Player(UUID uuid, Vector2D pos, Vector2D dir) {
        super(uuid, 0.25D, pos, dir);
        this.currentWeapon = new Shotgun();
        this.inputModule = new RemoteInput();
        this.teamMessagesToSend = new LinkedBlockingQueue<ByteBuffer>();
        this.allMessagesToSend = new LinkedBlockingQueue<>();
        
        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientController.FORWARD, this::moveForward);
        ACTION_MAPPINGS.put(ClientController.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientController.BACKWARD, this::moveBackward);
        ACTION_MAPPINGS.put(ClientController.ROTATE_CLOCKWISE, this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientController.LEFT, this::strafeLeft);
        ACTION_MAPPINGS.put(ClientController.RIGHT, this::strafeRight);
        ACTION_MAPPINGS.put(ClientController.SHOOT, this::shoot);

        setNetworkConsumers();

        zOffset = 100;
        moveSpeed = 0.120;
        strafeSpeed = 0.08;
        rotationSpeed = 0.06;

    }

    public Player(Vector2D pos, Vector2D dir) {
        this(UUID.randomUUID(), pos, dir);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y, double distanceTraveled) {
        super.render(pix, x, y, distanceTraveled);

        if (currentWeapon != null) {
            currentWeapon.render(pix, x, y, inertiaX, inertiaY, distanceTraveled, hasShot);
        }
    }

    private void setNetworkConsumers() {
        networkConsumers.put("setInputState", this::setInputState);
        networkConsumers.put("messageToTeam", this::messageToTeam);
        networkConsumers.put("messageToAll", this::messageToAll);
    }

    public void setInputState(ByteBuffer buf) {
    	this.timeToSend  = buf.getDouble();
        synchronized (inputState) {
            while (buf.hasRemaining()) {
                // take the control
                // using client protocol
                // to fetch the order
                ClientController control = null;
                try {
                    control = ClientProtocol.getByIndex(buf.getInt());
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
                Byte value = buf.get();
                inputState.put(control, value);
            }
        }


    }
    
    public void setInputState(byte[] inputs) {
        synchronized (inputState) {
            for(int i=0;i<inputs.length;i=i+2) {
                // take the control
                // using client protocol
                // to fetch the order
                ClientController control = null;
                try {
                    control = ClientProtocol.getByIndex(inputs[i]);
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
                Byte value = inputs[i+1];
                inputState.put(control, value);
            }
        }

    }
    
    public void messageToTeam(ByteBuffer buf){
    	String message = NetworkUtils.getStringFromBuf(buf);
    	message = "[" + this.team + "] " + this.name + ": " + message;
    	ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
    	NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
    	NetworkUtils.putStringIntoBuf(bf, "displayMessage");
    	NetworkUtils.putStringIntoBuf(bf, message);
    	this.teamMessagesToSend.offer(bf);
    }
    
    public ByteBuffer getTeamMessages(){
    	if(this.teamMessagesToSend.size()==0)
    		return null;
    	try {
			return this.teamMessagesToSend.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public void messageToAll(ByteBuffer buf){
    	String message = NetworkUtils.getStringFromBuf(buf);
    	message = "[all] " + this.name + ": " + message;
    	ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
    	NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
    	NetworkUtils.putStringIntoBuf(bf, "displayMessage");
    	NetworkUtils.putStringIntoBuf(bf, message);
    	this.allMessagesToSend.offer(bf);
    }
    
    public ByteBuffer getAllMessages(){
    	if(this.allMessagesToSend.size()==0)
    		return null;
    	try {
			return this.allMessagesToSend.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        hasShot = false;
        if (shootOnNextUpdate) {
            currentWeapon.fire(this);
            inertiaY += 60;
            hasShot = true;
            shootOnNextUpdate = false;
        }

        synchronized (inputState) {
            inputState = inputModule.updateInput(inputState, this);
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            for (Entry<ClientController, Byte> entry : inputState.entrySet()) {
            	// For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                }
            }
        }

        updateInertia();
        prevPos = position;
        prevDir = direction;
        currentWeapon.update();
    }

    private void updateInertia() {
        final double p = 0.1D;
        inertiaX += (int) (p * -inertiaX);
        inertiaY += (int) (p * -inertiaY);

        if (prevPos != null && prevDir != null) {

            Vector2D displacement = position.subtract(prevPos);
            Vector2D temp = new Vector2D(plane.getX() / plane.magnitude(), plane.getY() / plane.magnitude());
            double orthProjection = displacement.dot(temp);
            inertiaX -= orthProjection * currentWeapon.getInertiaCoeffX();

            temp = new Vector2D(direction.getX() / direction.magnitude(), direction.getY() / direction.magnitude());
            orthProjection = displacement.dot(temp);
            inertiaY += orthProjection * currentWeapon.getInertiaCoeffY();

            double prevDirTheta = Math.atan2(prevDir.getY(), prevDir.getX());
            double directionTheta = Math.atan2(direction.getY(), direction.getX());
            double diff = directionTheta - prevDirTheta;
            if (diff > Math.PI) {
                diff -= 2 * Math.PI;
            } else if (diff < -Math.PI) {
                diff += 2 * Math.PI;
            }

            inertiaX += currentWeapon.getInertiaCoeffX() * diff;
        }
    }

    private boolean shootOnNextUpdate;

    private void shoot() {
        if (currentWeapon == null)
            return;

        if (currentWeapon.canFire()) {
            shootOnNextUpdate = true;
        }
    }

    @Override
    public void onCollide(Player player) {
    }

    // TODO: serialize weapon etc.
    @Override
    public ByteBuffer serialize() {
        ByteBuffer bb = super.serialize();
        bb.putInt(shootOnNextUpdate ? 1 : 0);
        bb.putDouble(this.timeToSend);
        bb.putInt(this.respawn ? 1:0);
        this.respawn = false;
        return bb;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        super.deserialize(buf);
        shootOnNextUpdate = buf.getInt() == 1;
        this.timeToSend = buf.getDouble();
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getMinimapColor() {
        return 0xFFFFFF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.RED_PLAYER_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }

    @Override
    public void takeDamage(int damage, Entity inflictor) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            System.out.println(name + " was killed by " + inflictor.getName());
            this.sendSystemMessage(name, inflictor);
            GameEngine.getSingleton().getWorld().getGameManager().onPlayerDeath(this);
        }
    }

    void respawn(Vector2D spawnPos) {
        this.position = spawnPos;
        currentHealth = MAX_HEALTH;
        inputModule.onRespawn(this);
        this.respawn = true;
        System.out.println(name + " respawned.");
    }

    public void setInputModule(InputModule inp) {
        this.inputModule = inp;
    }

    public void setOnNextShot(boolean b){
    	this.shootOnNextUpdate = b;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    
}
