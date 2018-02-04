package com.knightlore.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.network.server.SendToClient;

public class NetworkObjectManager extends GameObject {
    private static NetworkObjectManager singleton;
    // Maps network objects to their most recent state. Used to check if the new
    // state has changed (if it hasn't, don't resend it).
    private Map<NetworkObject, byte[]> networkObjects = new HashMap<>();
    private List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();

    public void registerNetworkObject(NetworkObject obj) {
        this.networkObjects.put(obj, obj.serialize());
    }
    
    public void removeNetworkObject(NetworkObject obj) {
        this.networkObjects.remove(obj);
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
        for (Entry<NetworkObject, byte[]> e : networkObjects.entrySet()) {
            byte[] newState = e.getKey().serialize();
            if (areStatesDifferent(newState, e.getValue())) {
                for (SendToClient s : clientSenders)
                    s.sendState(newState);
                e.setValue(newState);
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    public boolean areStatesDifferent(byte[] lastState, byte[] currentState) {
        byte[] lastStateWithoutTimeToCompare = Arrays.copyOfRange(lastState,
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

    public static NetworkObjectManager getSingleton() {
        return singleton;
    }

}
