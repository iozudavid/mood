package com.knightlore.network.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.Camera;
import com.knightlore.utils.Vector2D;

public class ClientNetworkObjectManager extends NetworkObjectManager {
    private Map<UUID, NetworkObject> networkObjects = new HashMap<>();
    private UUID myPlayerUUID = null;

    public ClientNetworkObjectManager() {
        super();
        setNetworkConsumers();
    }

    private void setNetworkConsumers() {
        networkConsumers.put("registerPlayerIdentity",
                this::registerPlayerIdentity);
    }

    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
        System.out.println("client registering net obj");
        this.networkObjects.put(obj.getObjectId(), obj);
    }

    @Override
    public synchronized void removeNetworkObject(NetworkObject obj) {
        networkObjects.remove(obj.getObjectId());
    }

    public UUID getMyPlayerUUID() {
        return myPlayerUUID;
    }

    // Called remotely when a new network object is created on the server.
    @SuppressWarnings("unchecked")
    public void newObjCreated(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        String className = NetworkUtils.getStringFromBuf(buf);
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        if (networkObjects.containsKey(objID))
            // We already know about this object.
            return;
        double posX = buf.getDouble();
        double posY = buf.getDouble();
        Vector2D position = new Vector2D(posX, posY);
        // This bit represents whether the object is an Entity.
        if (buf.get() == 1) {
            double size = buf.getDouble();
            double dirX = buf.getDouble();
            double dirY = buf.getDouble();
            Vector2D direction = new Vector2D(dirX, dirY);
            try {
                Class<Entity> cls = (Class<Entity>) Class.forName(className);
                Constructor<Entity> cons = cls.getConstructor(UUID.class,
                        double.class, Vector2D.class, Vector2D.class);
                // Create the new object.
                cons.newInstance(objID, size, position, direction);
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                System.err.println("Class " + className
                        + " may not implement the required parameter signature for an Entity.");
                e.printStackTrace();
                return;
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                System.err.println(
                        "There was a problem while instantiating a new object of class "
                                + className + " with UUID " + objID);
                e.printStackTrace();
                return;
            }
        } else {
            try {
                Class<NetworkObject> cls = (Class<NetworkObject>) Class
                        .forName(className);
                Constructor<NetworkObject> cons = cls.getConstructor(UUID.class,
                        Vector2D.class);
                // Create the new object.
                cons.newInstance(objID, position);
            } catch (NoSuchMethodException | SecurityException
                    | ClassNotFoundException e) {
                System.err.println("Class " + className
                        + " may not implement the required parameter signature for a NetworkObject.");
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                System.err.println(
                        "There was a problem while instantiating a new object of class "
                                + className + " with UUID " + objID);
                e.printStackTrace();
                return;
            }
        }

    }

    // Called remotely when we receive a message from the server to tell us what
    // our UUID is.
    public void registerPlayerIdentity(ByteBuffer buf) {
        myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        Player player = null;
        while ((player = (Player) getNetworkObject(myPlayerUUID)) == null) {
            // Wait until we receive our player object from the server.
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for player ID.");
                return;
            }
        }
        Camera camera = new Camera(
                GameEngine.getSingleton().getRenderer().getMap());
        camera.setSubject(player);
        GameEngine.getSingleton().getRenderer().setCamera(camera);
    }
    
    @Override
    public void onCreate() {
        System.out.println("on create called for client");
        super.onCreate();
    }

    @Override
    public synchronized NetworkObject getNetworkObject(UUID uuid) {
        if (networkObjects.containsKey(uuid))
            return networkObjects.get(uuid);
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    }
}
