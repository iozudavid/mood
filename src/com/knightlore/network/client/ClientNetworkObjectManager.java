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
import com.knightlore.game.world.ClientWorld;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.Camera;

public class ClientNetworkObjectManager extends NetworkObjectManager {
    private Map<UUID, NetworkObject> networkObjects = new HashMap<>();
    private Player myPlayer = null;
    // Whether we've received enough information to start the game.
    private boolean finishedSetUp = false;

    private ClientWorld clientWorld;

    public ClientNetworkObjectManager(ClientWorld world) {
        super();
        this.clientWorld = world;
        setNetworkConsumers();
    }

    private void setNetworkConsumers() {
        networkConsumers.put("registerPlayerIdentity",
                this::registerPlayerIdentity);
        networkConsumers.put("newObjCreated", this::newObjCreated);
        networkConsumers.put("objDestroyed", this::objDestroyed);
        networkConsumers.put("receiveMapSeed", this::receiveMapSeed);
        networkConsumers.put("receiveReadySignal", this::receiveReadySignal);
    }

    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
        System.out.println("client registering net obj " + obj.getObjectId());
        this.networkObjects.put(obj.getObjectId(), obj);
    }

    @Override
    public synchronized void removeNetworkObject(NetworkObject obj) {
        networkObjects.remove(obj.getObjectId());
    }

    public Player getMyPlayer() {
        return myPlayer;
    }

    // Called remotely when a new network object is created on the server.
    // WARNING: dank reflection in this method.
    @SuppressWarnings("unchecked")
    public synchronized void newObjCreated(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        String className = NetworkUtils.getStringFromBuf(buf);
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        if (networkObjects.containsKey(objID))
            // We already know about this object.
            return;
        try {
            Class<Entity> cls = (Class<Entity>) Class.forName(className);
            // Build the new object.
            Method method = cls.getMethod("build", UUID.class,
                    ByteBuffer.class);
            // Static method, so pass null for object reference. The remainder
            // of the ByteBuffer constitutes
            // the state of the object.
            NetworkObject obj = (NetworkObject) method.invoke(null, objID, buf);
            // Entities need to exist in the world.
            if (obj instanceof Entity) {
                clientWorld.addEntity((Entity) obj);
            }
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            System.err.println(
                    "Error when attempting to call the static method build(UUID, ByteBuffer) on the class "
                            + className
                            + ". Are you sure you implemented it? See NetworkObject for details.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("The specified class " + className
                    + " could not be found.");
            e.printStackTrace();
        }
    }

    public synchronized void objDestroyed(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        NetworkObject toBeDestroyedObject = this.getNetworkObject(objID);
        this.networkObjects.remove(objID);
        toBeDestroyedObject.destroy();
        if (toBeDestroyedObject instanceof Entity) {
            clientWorld.removeEntity((Entity) toBeDestroyedObject);
        }
    }

    // Called remotely when we receive a message from the server to tell us what
    // the seed for the map to generate is.
    public synchronized void receiveMapSeed(ByteBuffer buf) {
        System.out.println("received map seed");
        long seed = buf.getLong();
        clientWorld.setUpWorld(seed);
    }

    // Called remotely when we receive a message from the server to tell us what
    // our UUID is.
    public synchronized void registerPlayerIdentity(ByteBuffer buf) {
        System.out.println("Registering player identity");
        UUID myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        myPlayer = (Player) networkObjects.get(myPlayerUUID);
        System.out.println("Created and set player.");
    }

    @Override
    public synchronized NetworkObject getNetworkObject(UUID uuid) {
        synchronized (this.networkObjects) {
            if (networkObjects.containsKey(uuid))
                return networkObjects.get(uuid);
        }
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    }

    public synchronized void receiveReadySignal(ByteBuffer buf) {
        Camera camera = new Camera(clientWorld.getMap());
        camera.setSubject(myPlayer);
        GameEngine.getSingleton().setCamera(camera);
        // We can now start the game.
        this.finishedSetUp = true;
    }

    @Override
    public void init() {
        super.init();

        // Wait for all information to be received - e.g., map seed, object
        // details, player identity.
        while (!finishedSetUp) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println(
                        "Unexpected interruption while waiting for game to be ready."
                                + e.getMessage());
            }
        }
    }

}
