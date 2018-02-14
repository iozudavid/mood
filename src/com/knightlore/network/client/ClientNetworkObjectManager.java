package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.INetworkable;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.Camera;

public class ClientNetworkObjectManager extends NetworkObjectManager {
    private Map<UUID, INetworkable> networkObjects = new HashMap<>();
    private UUID myPlayerUUID = null;
    
    public ClientNetworkObjectManager() {
        super();
        setNetworkConsumers();
    }
    
    private void setNetworkConsumers() {
        networkConsumers.put("registerPlayerIdentity", this::registerPlayerIdentity);
    }

    @Override
    public synchronized void registerNetworkObject(INetworkable obj) {
        System.out.println("client registering net obj");
        this.networkObjects.put(obj.getObjectId(), obj);
    }

    public UUID getMyPlayerUUID() {
        return myPlayerUUID;
    }

    @Override
    public synchronized void removeNetworkObject(INetworkable obj) {
        networkObjects.remove(obj.getObjectId());
    }

    public synchronized void registerPlayerIdentity(ByteBuffer buf) {
        myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        Player player = (Player) getNetworkObject(myPlayerUUID);
        Camera camera = new Camera(
                GameEngine.getSingleton().getRenderer().getMap());
        camera.setSubject(player);
        GameEngine.getSingleton().getRenderer().setCamera(camera);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {

    }

    // @Override
    private synchronized INetworkable getNetworkObject(UUID uuid) {
        if (networkObjects.containsKey(uuid))
            return networkObjects.get(uuid);
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    }
}
