package com.knightlore.network.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        networkConsumers.put("newObjCreated", this::newObjCreated);
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
        try {
            Class<Entity> cls = (Class<Entity>) Class.forName(className);
            // Build the new object.
            Method method = cls.getMethod("build", UUID.class);
            // Static method, so pass null.
            method.invoke(null, objID);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            System.err.println(
                    "Error when attempting to call the static method build(UUID) on the class "
                            + className
                            + ". Are you sure you implemented it? See NetworkObject for details.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("The specified class " + className
                    + " could not be found.");
            e.printStackTrace();
        }
    }

    // Called remotely when we receive a message from the server to tell us what
    // our UUID is.
    public void registerPlayerIdentity(ByteBuffer buf) {
        myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        Player player = (Player) getNetworkObject(myPlayerUUID);
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
