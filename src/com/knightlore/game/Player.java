package com.knightlore.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity implements IRenderable {

    private Weapon currentWeapon;

    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientControl, Runnable> ACTION_MAPPINGS = new HashMap<>();
    private java.util.Map<ClientControl, Byte> inputState = new HashMap<>();

    private final Map<ServerControl, CameraGetterInterface> controlGettersMap;
    private final Map<ServerControl, CameraSetterInterface> controlSettersMap;
    private static volatile boolean finished = false;

    public Player(com.knightlore.game.area.Map map, Vector2D pos, Vector2D dir) {
        super(map, 0.33D, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE, pos, dir);
        this.currentWeapon = new Shotgun();
        
        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientControl.FORWARD, this::moveForward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientControl.BACKWARD, this::moveBackward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_CLOCKWISE, this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientControl.LEFT, this::strafeLeft);
        ACTION_MAPPINGS.put(ClientControl.RIGHT, this::strafeRight);

        this.controlGettersMap = new HashMap<>();
        this.controlGettersMap.put(ServerControl.XPOS, this::getxPos);
        this.controlGettersMap.put(ServerControl.YPOS, this::getyPos);
        this.controlGettersMap.put(ServerControl.XDIR, this::getxDir);
        this.controlGettersMap.put(ServerControl.YDIR, this::getyDir);
        this.controlGettersMap.put(ServerControl.XPLANE, this::getxPlane);
        this.controlGettersMap.put(ServerControl.YPLANE, this::getyPlane);

        this.controlSettersMap = new HashMap<>();
        this.controlSettersMap.put(ServerControl.XPOS, this::setxPos);
        this.controlSettersMap.put(ServerControl.YPOS, this::setyPos);
        this.controlSettersMap.put(ServerControl.XDIR, this::setxDir);
        this.controlSettersMap.put(ServerControl.YDIR, this::setyDir);
        this.controlSettersMap.put(ServerControl.XPLANE, this::setxPlane);
        this.controlSettersMap.put(ServerControl.YPLANE, this::setyPlane);
        Player.this.finished = true;

    }

    public void setInputState(Map<ClientControl, Byte> inputState) {
        this.inputState = inputState;
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        pix.fillRect(0x000000, (int) this.position.getX(), (int) this.position.getY(), 10, 50);
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

    @Override
    public byte[] serialize(boolean disconnect) {
        if (Player.this.finished == false) {
            System.out.println("Constructor didn't finish");
            return null;
        }

        byte[] thisState = new byte[ServerProtocol.TOTAL_LENGTH];

        // Prepend metadata to the state array.
        byte[] metadata = ServerProtocol.getMetadata();
        for (int i = 0; i < ServerProtocol.METADATA_LENGTH; i++) {
            thisState[i] = metadata[i];
        }

        byte[] playerID = ServerProtocol.uuidAsBytes(getObjectId());
        // add player id to the packet
        for (int i = ServerProtocol.METADATA_LENGTH; i < ServerProtocol.METADATA_LENGTH
                + ServerProtocol.OBJECTID_LENGTH; i++) {
            thisState[i] = playerID[i - ServerProtocol.METADATA_LENGTH];
        }

        if (disconnect) {
            for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH; i++) {
                thisState[i] = ServerProtocol.disconnectedState[i - ServerProtocol.MESSAGE_STARTING_POINT];
            }
            // if disconnect then end it here with return statement
            return thisState;
        }

        // adding player stats from camera class
        int loopsNumber = 0;
        try {
            for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH;) {
                // taking the current control
                int positionCurrentCommand = loopsNumber;
                ServerControl currentControl = ServerProtocol.getControlByPosition(positionCurrentCommand);
                Integer[] indexes = ServerProtocol.getIndexesByControl(currentControl);
                int startingIndex = indexes[0];
                int endingIndex = indexes[1];

                CameraGetterInterface cameraReference = this.controlGettersMap.get(currentControl);
                if (cameraReference == null)
                    return null;
                double cameraResult = cameraReference.accessDataFromCamera();
                byte[] convertedCameraValue = ServerProtocol.doubleToByteArray(cameraResult);

                for (int j = startingIndex + ServerProtocol.MESSAGE_STARTING_POINT; j < endingIndex
                        + ServerProtocol.MESSAGE_STARTING_POINT; j++) {
                    thisState[j] = convertedCameraValue[j - startingIndex - ServerProtocol.MESSAGE_STARTING_POINT];
                }
                i += endingIndex - startingIndex;
                loopsNumber++;

            }
        } catch (IOException e) {
            System.err.println("Index not good...");
            e.printStackTrace();
        }
        return thisState;

    }

    @Override
    public void deserialize(ServerCommand command) {
        // TODO: only deserialise if the command's timestamp is recent enough.
        Double val;
        for (ServerControl c : ServerControl.values())
            if ((val = command.getValueByControl(c)) != null)
                this.controlSettersMap.get(c).setDataOnCamera(val);

    }

    @FunctionalInterface
    public static interface CameraGetterInterface {
        double accessDataFromCamera();
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    @FunctionalInterface
    public static interface CameraSetterInterface {
        void setDataOnCamera(double val);
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
