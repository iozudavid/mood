package com.knightlore.network.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameWorld;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.Camera;
import com.knightlore.render.minimap.IMinimapObject;

public class ClientNetworkObjectManager extends NetworkObjectManager {
    private Map<UUID, NetworkObject> networkObjects = new HashMap<>();
    private UUID myPlayerUUID = null;

    public ClientNetworkObjectManager(GameWorld world) {
        super(world);
        setNetworkConsumers();
    }

    private void setNetworkConsumers() {
        networkConsumers.put("registerPlayerIdentity", this::registerPlayerIdentity);
        networkConsumers.put("newObjCreated", this::newObjCreated);
        networkConsumers.put("objDestroyed", this::objDestroyed);
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

    public UUID getMyPlayerUUID() {
        return myPlayerUUID;
    }

    // Called remotely when a new network object is created on the server.
    // WARNING: dank reflection in this method.
    @SuppressWarnings("unchecked")
    public void newObjCreated(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        String className = NetworkUtils.getStringFromBuf(buf);
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        synchronized (this.networkObjects) {
            if (networkObjects.containsKey(objID))
                // We already know about this object.
                return;
        }
        try {
            Class<Entity> cls = (Class<Entity>) Class.forName(className);
            // Build the new object.
            Method method = cls.getMethod("build", UUID.class, ByteBuffer.class);
            // Static method, so pass null for object reference. The remainder
            // of the ByteBuffer constitutes
            // the state of the object.
            NetworkObject obj = (NetworkObject) method.invoke(null, objID, buf);
            // Entities need to exist in the world.
            if (obj instanceof Entity) {
                world.addEntity((Entity) obj);
            }
            if (obj instanceof IMinimapObject) {
                world.addMinimapObj((IMinimapObject) obj);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            System.err.println("Error when attempting to call the static method build(UUID, ByteBuffer) on the class "
                    + className + ". Are you sure you implemented it? See NetworkObject for details.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("The specified class " + className + " could not be found.");
            e.printStackTrace();
        }
    }

    public void objDestroyed(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        String className = NetworkUtils.getStringFromBuf(buf);
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        synchronized (this.networkObjects) {
            NetworkObject toBeDestroyedObject = this.getNetworkObject(objID);
            this.networkObjects.remove(objID);
            toBeDestroyedObject.destroy();
            if (toBeDestroyedObject instanceof Entity) {
                world.removeEntity((Entity) toBeDestroyedObject);
            }
            if (toBeDestroyedObject instanceof IMinimapObject) {
                world.removeMinimapObj((IMinimapObject) toBeDestroyedObject);
            }
        }
    }

    // Called remotely when we receive a message from the server to tell us what
    // our UUID is.
    public void registerPlayerIdentity(ByteBuffer buf) {
        System.out.println("Registering player identity");
        myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        // wait until the network object
        // is added to the list
        // this will avoid getting a null value as the player
        while (!networkObjects.containsKey(myPlayerUUID)) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println(
                        "Unexpected interruption while waiting for network obj to be registered " + e.getMessage());
            }
        }
        Player player = (Player) getNetworkObject(myPlayerUUID);
        GameEngine.getSingleton().getCamera().setSubject(player);
        System.out.println("Created and set player.");
    }

    @Override
    public synchronized NetworkObject getNetworkObject(UUID uuid) {
        synchronized (this.networkObjects) {
            if (networkObjects.containsKey(uuid))
                return networkObjects.get(uuid);
        }
        System.err.println("No network object with UUID " + uuid + " could be found on this client.");
        return null;
    }

    public boolean isPlayerReady() {
        return myPlayerUUID != null;
    }

}
