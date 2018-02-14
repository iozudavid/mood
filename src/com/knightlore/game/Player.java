package com.knightlore.game;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity implements IRenderable {

    private Weapon currentWeapon;

    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientControl, Runnable> ACTION_MAPPINGS = new HashMap<>();
    private java.util.Map<ClientControl, Byte> inputState = new HashMap<>();
    //private volatile boolean finished = false;

    public Player(UUID uuid, com.knightlore.game.area.Map map, Vector2D pos,
            Vector2D dir) {
        super(uuid, map, 0.33D, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE,
                pos, dir);
        this.currentWeapon = new Shotgun();
        
        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientControl.FORWARD, this::moveForward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientControl.BACKWARD, this::moveBackward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_CLOCKWISE, this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientControl.LEFT, this::strafeLeft);
        ACTION_MAPPINGS.put(ClientControl.RIGHT, this::strafeRight);
        
        setNetworkConsumers();
        
        //Player.this.finished = true;
    }

    public Player(com.knightlore.game.area.Map map, Vector2D pos,
            Vector2D dir) {
        this(UUID.randomUUID(), map, pos, dir);
    }
    
    private void setNetworkConsumers() {
        networkConsumers.put("setInputState", this::setInputState);
    }

    public void setInputState(ByteBuffer buf) {
        synchronized (inputState) {
            while (buf.hasRemaining()) {
                ClientControl control = ClientControl.values()[buf.getInt()];
                Byte value = buf.get();
                inputState.put(control, value);
            }
        }

    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        pix.fillRect(0x000000, (int) this.position.getX(),
                (int) this.position.getY(), 10, 50);
    }

    @Override
    public void onUpdate() {
        synchronized (inputState) {
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            boolean updated = false;
            for (Entry<ClientControl, Byte> entry : inputState.entrySet())
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
    public int getDrawSize() {
        return 0;
    }

    @Override
    public int getMinimapColor() {
        return 0;
    }

}
