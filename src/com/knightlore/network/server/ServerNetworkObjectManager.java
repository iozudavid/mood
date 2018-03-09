package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.game.world.ServerWorld;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Tuple;

public class ServerNetworkObjectManager extends NetworkObjectManager {
    // How often to send an update of state, rather than just if the
    // states have changed. A value of 100 means that in every 100 loops,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;
    // Maps network objects to their most recent state. Used to check if the new
    // state has changed (if it hasn't, don't resend it).
    private Map<UUID, Tuple<NetworkObject, ByteBuffer>> networkObjects = new HashMap<>();
    private List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();
    // Counter for REGULAR_UPDATE_FREQ
    private int updateCount = 1;

    private ServerWorld serverWorld;
    
    //in order to implement interpolation
    //we need to send player's state to other players
    //a tick later
    //to make game playable
    private Map<UUID, ByteBuffer> statesToBeSent;

    public ServerNetworkObjectManager(ServerWorld world) {
        super();
        this.serverWorld = world;
        this.statesToBeSent = new LinkedHashMap<>();
    }

    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
        UUID uuid = obj.getObjectId();
        this.networkObjects.put(uuid, new Tuple<>(obj, obj.serialize()));
        // Notify all clients of the newly-created object.
        this.sendToAllClients(getObjectCreationMessage(obj));
    }

    private ByteBuffer getObjectCreationMessage(NetworkObject obj) {
        // Leave room for the state (1 full buffer size), and the additional
        // overhead as well.
        ByteBuffer buf = ByteBuffer.allocate(2 * BYTE_BUFFER_DEFAULT_SIZE);
        // Send the message to the ClientNetworkObjectManager.
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        // The remote method to call.
        NetworkUtils.putStringIntoBuf(buf, "newObjCreated");
        // Let the client know which class it needs to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getClientClassName());
        // UUID of object to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getObjectId().toString());
        // Send the current state when sending the creation message.
        ByteBuffer state = networkObjects.get(obj.getObjectId()).y;
        int stateLength = state.position();
        buf.put(Arrays.copyOfRange(state.array(), 0, stateLength));
        return buf;
    }

    private ByteBuffer getObjectDestroyMessage(NetworkObject obj) {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_DEFAULT_SIZE);
        // Send the message to the ClientNetworkObjectManager.
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        // The remote method to call.
        NetworkUtils.putStringIntoBuf(buf, "objDestroyed");
        // UUID of object to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getObjectId().toString());
        return buf;
    }

    // Notify a particular client of all existing objects.
    private synchronized void notifyOfAllObjs(SendToClient sender) {
        System.out.println("Notifying new client of all objects");
        for (Entry<UUID, Tuple<NetworkObject, ByteBuffer>> e : networkObjects
                .entrySet()) {
            NetworkObject obj = e.getValue().x;
            ByteBuffer msg = getObjectCreationMessage(obj);
            sender.send(msg);
        }
    }

    // call this function to disconnect client
    // with the given id
    // onUpdate will do the job
    public void disconnectClient(UUID uuid) {
        // TODO
    }

    public synchronized void removeNetworkObject(NetworkObject obj) {
        UUID uuid = obj.getObjectId();
        // Notify all clients of the destroyed object.
        this.sendToAllClients(getObjectDestroyMessage(obj));
        this.networkObjects.remove(uuid);
    }

    public UUID registerClientSender(SendToClient sender) {
        // Tell the player the seed to generate the map from.
        sendMapSeed(sender);
        Player newPlayer = serverWorld.createPlayer();
        // First, tell the client what objects are on the server.
        notifyOfAllObjs(sender);
        // Now, tell the player who they are.
        sendPlayerIdentity(sender, newPlayer);
        // Tell the player they're now ready to start.
        sendReadySignal(sender);

        synchronized (this.clientSenders) {
            this.clientSenders.add(sender);
        }

        return newPlayer.getObjectId();
    }

    private void sendReadySignal(SendToClient sender) {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "receiveReadySignal");
        sender.send(buf);
    }

    private void sendMapSeed(SendToClient sender) {
        System.out.println("Sending map seed");
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "receiveMapSeed");
        buf.putLong(serverWorld.getMap().getSeed());
        sender.send(buf);
    }

    public void sendPlayerIdentity(SendToClient sender, Player player) {
        System.out.println("sending player identity " + player.getObjectId());
        ByteBuffer buf = ByteBuffer
                .allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "registerPlayerIdentity");
        NetworkUtils.putStringIntoBuf(buf, player.getObjectId().toString());
        sender.send(buf);
    }

    public void removeClientSender(SendToClient obj) {
        synchronized (this.clientSenders) {
            this.clientSenders.remove(obj);
        }
    }

    public void init() {
        super.init();

        GameEngine.ticker.addTickListener(new TickListener() {

            @Override
            public long interval() {
                return 1;
            }

            @Override
            public void onTick() {
                syncStates();
            }

        });
        // Initialise the world without specifying a seed.
        serverWorld.setUpWorld(null);
    }

    private synchronized void syncStates() {
        // Iterate through each networkable object. If its state
        // has changed, send the new state to each client.
        for (Entry<UUID, Tuple<NetworkObject, ByteBuffer>> t : networkObjects
                .entrySet()) {
        	for(Entry<UUID,ByteBuffer> b : this.statesToBeSent.entrySet()){
        		this.sendToClients(b.getKey(),b.getValue());
        	}
        	this.statesToBeSent.clear();
            ByteBuffer newState = t.getValue().x.serialize();
            // Send state either if we're due a regular update, or if the
            // state has changed.
            synchronized (this.clientSenders) {
                // check if they are equal
                // by transforming into arrays
                // and use the standard library
                if (updateCount >= REGULAR_UPDATE_FREQ || !Arrays
                        .equals(newState.array(), t.getValue().y.array())) {
                    this.sendToOneClient(t.getKey(),newState);
                    this.statesToBeSent.put(t.getKey(), newState);
                    // networkObjects.put(t.getKey(), new
                    // Tuple<>(t.getValue().x, newState));
                    t.getValue().y = newState;
                }
            }

        }
        if (updateCount >= REGULAR_UPDATE_FREQ)
            updateCount = 1;
        else
            updateCount++;
    }

    public synchronized NetworkObject getNetworkObject(UUID uuid) {
        if (networkObjects.containsKey(uuid))
            return networkObjects.get(uuid).x;
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;

    }

    // Send a message to all connected clients.
    private void sendToClients(UUID uuid, ByteBuffer buf) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders) {
                if(s.getUUID().equals(uuid))
                	continue;
            	s.send(buf);
            }
        }
    }
    
    private void sendToAllClients(ByteBuffer buf) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders) {
            	s.send(buf);
            }
        }
    }
    
    private void sendToOneClient(UUID uuid, ByteBuffer buf) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders) {
            	if(s.getUUID().equals(uuid)){
            		s.send(buf);
            		return;
            	}
            }
        }
    }

}
