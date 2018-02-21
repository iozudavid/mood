package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.game.Player;
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

    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
		synchronized (this.networkObjects) {
			UUID uuid = obj.getObjectId();
			this.networkObjects.put(uuid, new Tuple<>(obj, obj.serialize()));
			// Notify all clients of the newly-created object.
			this.sendToClients(getObjectCreationMessage(obj));
		}
    }

    private ByteBuffer getObjectCreationMessage(NetworkObject obj) {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_MAX_SIZE);
        // Send the message to the ClientNetworkObjectManager.
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        // The remote method to call.
        NetworkUtils.putStringIntoBuf(buf, "newObjCreated");
        // Let the client know which class it needs to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getClass().getName());
        // UUID of object to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getObjectId().toString());
        return buf;
    }

    // Notify a particular client of all existing objects.
    private synchronized void notifyOfAllObjs(SendToClient sender) {
        System.out.println("Notifying new client of all objects");
		synchronized (this.networkObjects) {
			for (Entry<UUID, Tuple<NetworkObject, ByteBuffer>> e : networkObjects.entrySet()) {
				NetworkObject obj = e.getValue().x;
				ByteBuffer msg = getObjectCreationMessage(obj);
				sender.send(msg);
			}
		}
    }

    // call this function to disconnect client
    // with the given id
    // onUpdate will do the job
    public void disconnectClient(UUID uuid) {
        // TODO
    }

    public synchronized void removeNetworkObject(NetworkObject obj) {
		synchronized (this.networkObjects) {
			networkObjects.remove(obj.getObjectId());
		}
    }

    public void registerClientSender(SendToClient sender) {
        synchronized (this.clientSenders) {
            this.clientSenders.add(sender);
        }
        // First, tell the client what objects are on the server.
        notifyOfAllObjs(sender);

        System.out.println(
                "sending player identity" + System.currentTimeMillis());
        // Now, tell the player who they are.
        ByteBuffer buf = ByteBuffer
                .allocate(NetworkObject.BYTE_BUFFER_MAX_SIZE);
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "registerPlayerIdentity");
        NetworkUtils.putStringIntoBuf(buf, sender.uuid.toString());
        sender.send(buf);
    }

    public void removeClientSender(SendToClient obj) {
        synchronized (this.clientSenders) {
            this.clientSenders.remove(obj);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        syncStates();
    }

    private synchronized void syncStates() {
        // Iterate through each networkable object. If its state
        // has changed, send the new state to each client.
		synchronized (this.networkObjects) {
			for (Entry<UUID, Tuple<NetworkObject, ByteBuffer>> t : networkObjects.entrySet()) {
				ByteBuffer newState = t.getValue().x.serialize();
				// Send state either if we're due a regular update, or if the
				// state has changed.
				synchronized (this.clientSenders) {
					//check if they are equal
					//by transforming into arrays
					//and use the standard library
					if (updateCount >= REGULAR_UPDATE_FREQ || !Arrays.equals(newState.array(), t.getValue().y.array())) {
						this.sendToClients(newState);
						t.getValue().y = newState;
					}
				}
			}
		}
        if (updateCount >= REGULAR_UPDATE_FREQ)
            updateCount = 1;
        else
            updateCount++;
    }

    public synchronized NetworkObject getNetworkObject(UUID uuid) {
		synchronized (this.networkObjects) {
			if (networkObjects.containsKey(uuid))
				return networkObjects.get(uuid).x;
		}
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    	
    }

    // Send a message to all connected clients.
    private void sendToClients(ByteBuffer buf) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders)
                s.send(buf);
        }
    }

}
