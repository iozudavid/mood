package com.knightlore.game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.ImmutableMap;
import com.knightlore.GameSettings;
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
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.animation.PlayerAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.PlayerGraphicMatrix;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity {

    private PlayerAnimation animation = new PlayerAnimation(PlayerGraphicMatrix.getGraphic(
            PlayerGraphicMatrix.Color.BLUE, PlayerGraphicMatrix.Weapon.PISTOL, PlayerGraphicMatrix.Stance.MOVE));

    public static final int MAX_HEALTH = 100;
    // Maps all inputs that the player could be making to their values.
    private final ImmutableMap<ClientController, Runnable> ACTION_MAPPINGS = ImmutableMap
            .<ClientController, Runnable>builder().put(ClientController.FORWARD, this::moveForward)
            .put(ClientController.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise)
            .put(ClientController.BACKWARD, this::moveBackward)
            .put(ClientController.ROTATE_CLOCKWISE, this::rotateClockwise).put(ClientController.LEFT, this::strafeLeft)
            .put(ClientController.RIGHT, this::strafeRight).put(ClientController.SHOOT, this::shoot).build();

    private final BlockingQueue<ByteBuffer> teamMessagesToSend = new LinkedBlockingQueue<>();
    private final BlockingQueue<ByteBuffer> allMessagesToSend = new LinkedBlockingQueue<>();
    private Map<ClientController, Byte> inputState = new HashMap<>();

    private int score = 0;
    private int currentHealth = MAX_HEALTH;
    private Weapon currentWeapon = new Shotgun();
    private boolean shootOnNextUpdate;

    private boolean hasShot;
    private Vector2D prevPos, prevDir;
    private int inertiaX = 0, inertiaY = 0;
    private InputModule inputModule = new RemoteInput();

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
        setNetworkConsumers();

        zOffset = 100;
        moveSpeed = 0.060;
        strafeSpeed = 0.08;
        rotationSpeed = 0.06;
    }

    public Player(Vector2D pos, Vector2D dir) {
        this(UUID.randomUUID(), pos, dir);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y, double distanceTraveled) {
        super.render(pix, x, y, distanceTraveled);
        currentWeapon.render(pix, x, y, inertiaX, inertiaY, distanceTraveled, hasShot);
    }

    private void setNetworkConsumers() {
        networkConsumers.put("setInputState", this::setInputState);
        networkConsumers.put("messageToTeam", this::messageToTeam);
        networkConsumers.put("messageToAll", this::messageToAll);
    }

    public void setInputState(ByteBuffer buf) {
        synchronized (inputState) {
            while (buf.hasRemaining()) {
                try {
                    ClientController control = ClientProtocol.getByIndex(buf.getInt());
                    Byte value = buf.get();
                    inputState.put(control, value);
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
            }
        }

    }

    public void messageToTeam(ByteBuffer buf) {
        String message = NetworkUtils.getStringFromBuf(buf);
        message = "[" + this.team + "] " + this.name + ": " + message;
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.teamMessagesToSend.offer(bf);
    }

    public Optional<ByteBuffer> getTeamMessages() {
        if (this.teamMessagesToSend.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.teamMessagesToSend.take());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void messageToAll(ByteBuffer buf) {
        String message = NetworkUtils.getStringFromBuf(buf);
        message = "[all] " + this.name + ": " + message;
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.allMessagesToSend.offer(bf);
    }

    public Optional<ByteBuffer> getAllMessages() {
        if (this.allMessagesToSend.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.allMessagesToSend.take());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Graphic getGraphic(Vector2D playerPos) {
        DirectionalSprite frame = animation.getFrame();
        return frame.getCurrentGraphic(position, direction, playerPos);
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

        if (prevDir != null && prevPos != null) {
            // The difference between our previous and new positions.
            Vector2D displacement = position.subtract(prevPos);
            updateInertia(displacement);
            animation.update(displacement.magnitude());
        }

        prevPos = position;
        prevDir = direction;
        currentWeapon.update();
    }

    private void updateInertia(Vector2D displacement) {
        if (!GameSettings.MOTION_BOB) {
            inertiaX = 0;
            inertiaY = 0;
            return;
        }

        final double p = 0.1D;
        inertiaX += (int) (p * -inertiaX);
        inertiaY += (int) (p * -inertiaY);
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

    private void shoot() {
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
        return bb;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        super.deserialize(buf);
        shootOnNextUpdate = buf.getInt() == 1;
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
        return DirectionalSprite.PLAYER_DIRECTIONAL_SPRITE;
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

    public void increaseScore(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        score += value;
    }

    public void decreaseScore(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        score -= value;
    }

    public int getScore() {
        return score;
    }

    void respawn(Vector2D spawnPos) {
        this.position = spawnPos;
        currentHealth = MAX_HEALTH;
        inputModule.onRespawn(this);
        System.out.println(name + " respawned.");
    }

    public void setInputModule(InputModule inp) {
        this.inputModule = inp;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
}
