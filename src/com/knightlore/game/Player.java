package com.knightlore.game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity {

    private final int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    private Weapon currentWeapon;

    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientController, Runnable> ACTION_MAPPINGS = new HashMap<>();
    private java.util.Map<ClientController, Byte> inputState = new HashMap<>();
    // private volatile boolean finished = false;

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

        // Player.this.finished = true;
    }

    public Player(Vector2D pos, Vector2D dir) {
        this(UUID.randomUUID(), pos, dir);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y, double distanceTraveled) {
        super.render(pix, x, y, distanceTraveled);

        final int SCALE = 6;
        Graphic g = currentWeapon.getGraphic();
        final int width = g.getWidth() * SCALE, height = g.getHeight() * SCALE;

        final int weaponBobX = 20, weaponBobY = 30;

        int xx = x + (pix.getWidth() - width) / 2;
        int yy = pix.getHeight() - height + 52 * SCALE;

        int xOffset = (int) (Math.cos(distanceTraveled) * weaponBobX);
        int yOffset = (int) (Math.abs(Math.sin(distanceTraveled) * weaponBobY));

        pix.drawGraphic(g, xx + xOffset, yy + yOffset, SCALE, SCALE);
    }

    private void setNetworkConsumers() {
        networkConsumers.put("setInputState", this::setInputState);
    }

    public void setInputState(ByteBuffer buf) {
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

    @Override
    public void onUpdate() {
        synchronized (inputState) {
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            boolean updated = false;
            for (Entry<ClientController, Byte> entry : inputState.entrySet())
                // For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                    updated = true;
                }

            if (updated) {
                // updateMotionOffset();
            }
        }
    }

    private void shoot() {
        System.out.println("SHOOT");
    }

    @Override
    public void onCollide(Player player) {
    }

    // TODO: serialize weapon etc.
    @Override
    public ByteBuffer serialize() {
        return super.serialize();
    }

    @Override
    public void deserialize(ByteBuffer buffer) {
        super.deserialize(buffer);
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

}
