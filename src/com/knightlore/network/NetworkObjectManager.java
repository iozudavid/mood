package com.knightlore.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameObject;
import com.knightlore.game.Player;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.network.server.SendToClient;
import com.knightlore.render.Camera;
import com.knightlore.utils.Tuple;

public class NetworkObjectManager extends GameObject {
    private static NetworkObjectManager singleton;
    // Maps network objects to their most recent state. Used to check if the new
    // state has changed (if it hasn't, don't resend it).
    private Map<UUID, Tuple<NetworkObject, byte[]>> networkObjects = new HashMap<>();
    private List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();

    public NetworkObjectManager() {
        super();
        singleton = this;
    }

    public void registerNetworkObject(NetworkObject obj) {
        this.networkObjects.put(obj.objectUniqueID, new Tuple<>(obj, null));
    }

    public void removeNetworkObject(NetworkObject obj) {
        Iterator<Entry<UUID, Tuple<NetworkObject, byte[]>>> it = this.networkObjects.entrySet().iterator();
        Entry<UUID, Tuple<NetworkObject, byte[]>> next = null;
        while (it.hasNext()){
            next = it.next();
            if(next.getValue().x == obj){
                it.remove();
            }
        }
    }

    public NetworkObject getNetworkObject(UUID uuid) {
        if(networkObjects.containsKey(uuid))
            return networkObjects.get(uuid).x;
        Camera camera = new Camera(4.5, 4.5, 1, 0, 0,
                Camera.FIELD_OF_VIEW,
                GameEngine.getSingleton().getWorld().getMap());
        networkObjects.put(uuid, new Tuple<NetworkObject, byte[]>(new Player(uuid, camera), null));
        return getNetworkObject(uuid);
    }

    public void registerClientSender(SendToClient obj) {
        this.clientSenders.add(obj);
    }

    public void removeClientSender(SendToClient obj) {
        this.clientSenders.remove(obj);
    }

    @Override
    public void onCreate() {
        System.out.println("NetworkObjectManager singleton created.");

    }

    @Override
    public void onUpdate() {
        // Iterate through each serialisable network object. If its state has
        // changed, send the new state to each client.
        for (Tuple<NetworkObject, byte[]> t : networkObjects.values()) {
            byte[] newState = t.x.serialize();
            if (areStatesDifferent(newState, t.y)) {
                for (SendToClient s : clientSenders)
                    s.sendState(newState);
                t.y = newState;
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    public boolean areStatesDifferent(byte[] x, byte[] currentState) {
        if (x == null || currentState == null)
            return true;
        byte[] lastStateWithoutTimeToCompare = Arrays.copyOfRange(x,
                ServerProtocol.METADATA_LENGTH, ServerProtocol.TOTAL_LENGTH);
        byte[] currentStateWithoutTimeToCompare = Arrays.copyOfRange(
                currentState, ServerProtocol.METADATA_LENGTH,
                ServerProtocol.TOTAL_LENGTH);
        if (lastStateWithoutTimeToCompare.length != currentStateWithoutTimeToCompare.length)
            return true;
        for (int i = 0; i < lastStateWithoutTimeToCompare.length; i++) {
            if (lastStateWithoutTimeToCompare[i] != currentStateWithoutTimeToCompare[i])
                return true;
        }
        return false;

    }

    public synchronized static NetworkObjectManager getSingleton() {
        return singleton;
    }

}
