package com.knightlore.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameObject;
import com.knightlore.game.Player;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.network.server.SendToClient;
import com.knightlore.render.Camera;
import com.knightlore.utils.Tuple;
import com.knightlore.utils.Vector2D;

public class NetworkObjectManager extends GameObject {
    // How often to send an update of state, rather than just if the
    // states have changed. A value of 100 means that in every 100 loops,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;
    private static NetworkObjectManager singleton;
    // Maps network objects to their most recent state. Used to check if the new
    // state has changed (if it hasn't, don't resend it).
    private Map<UUID, Tuple<NetworkObject, byte[]>> networkObjects = new HashMap<>();
    private List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();
    // map to keep track of the alive objects
    // if an entry has a true value then that object is no longer alive
    private Map<UUID, Tuple<NetworkObject, Boolean>> disconnectedObjects = new HashMap<>();
    // Counter for REGULAR_UPDATE_FREQ
    private int updateCount = 1;

    public NetworkObjectManager() {
        super();
        singleton = this;
    }

    public void registerNetworkObject(NetworkObject obj) {
		synchronized (this.networkObjects) {
			this.networkObjects.put(obj.objectUniqueID, new Tuple<>(obj, null));
		}
			// is alive at the register time
			// so initialized it with false
		synchronized (this.disconnectedObjects) {
			this.disconnectedObjects.put(obj.objectUniqueID, new Tuple<>(obj, false));
		}
    }

    // call this function to disconnect client
    // with the given id
    // onUpdate will do the job
    public void disconnectClient(UUID uuid) {
		synchronized (this.disconnectedObjects) {
			Tuple<NetworkObject, Boolean> tuple = this.disconnectedObjects.get(uuid);
			tuple.y = true;
			this.disconnectedObjects.replace(uuid, tuple);
		}
    }

    // remove it from both maps
    public void removeNetworkObject(NetworkObject obj) {
		synchronized (this.networkObjects) {
			Iterator<Entry<UUID, Tuple<NetworkObject, byte[]>>> networkObjIt = this.networkObjects.entrySet()
					.iterator();
			Entry<UUID, Tuple<NetworkObject, byte[]>> nextNetObj = null;
			while (networkObjIt.hasNext()) {
				nextNetObj = networkObjIt.next();
				if (nextNetObj.getValue().x == obj) {
					networkObjIt.remove();
				}
			}
		}
		synchronized (this.disconnectedObjects) {
			Iterator<Entry<UUID, Tuple<NetworkObject, Boolean>>> networkDiscObjIt = this.disconnectedObjects.entrySet()
					.iterator();
			Entry<UUID, Tuple<NetworkObject, Boolean>> nextDiscNetObj = null;
			while (networkDiscObjIt.hasNext()) {
				nextDiscNetObj = networkDiscObjIt.next();
				if (nextDiscNetObj.getValue().x == obj) {
					networkDiscObjIt.remove();
				}
			}
		}

    }

    public NetworkObject getNetworkObject(UUID uuid) {
		synchronized (this.networkObjects) {
			if (networkObjects.containsKey(uuid))
				return networkObjects.get(uuid).x;
			Vector2D pos = GameSettings.spawnPos;
			Camera camera = new Camera(pos.getX(), pos.getY(), 1, 0, 0, Camera.FIELD_OF_VIEW,
					GameEngine.getSingleton().getRenderer().getMap());
			networkObjects.put(uuid, new Tuple<NetworkObject, byte[]>(new Player(uuid, camera), null));
			return getNetworkObject(uuid);
		}
    }

    public void registerClientSender(SendToClient obj) {
		synchronized (this.clientSenders) {
			this.clientSenders.add(obj);
		}
    }

    public void removeClientSender(SendToClient obj) {
		synchronized (this.clientSenders) {
			this.clientSenders.remove(obj);
		}
    }

    @Override
    public void onCreate() {
    }

	@Override
	public void onUpdate() {
		synchronized (this.networkObjects) {
			// Iterate through each serialisable network object. If its state
			// has
			// changed, send the new state to each client.
			ArrayList<NetworkObject> toRemoveObjects = new ArrayList<>();
			for (Entry<UUID, Tuple<NetworkObject, byte[]>> t : networkObjects.entrySet()) {
				boolean disconnect;
				synchronized (this.disconnectedObjects) {
					disconnect = this.disconnectedObjects.get(t.getKey()).y;
				}
				byte[] newState = t.getValue().x.serialize(disconnect);
				// Send state either if we're due a regular update, or if the
				// state
				// has changed.
				synchronized (this.clientSenders) {
					if (updateCount >= REGULAR_UPDATE_FREQ
							|| NetworkUtils.areStatesDifferent(newState, t.getValue().y)) {
						for (SendToClient s : clientSenders)
							s.sendState(newState);
						if (!disconnect) {
							t.getValue().y = newState;
							continue;
						}
						// if the user is disconnected
						// no point in keep it in memory, so add to remove list
						toRemoveObjects.add(t.getValue().x);
					}
				}
			}
			for (NetworkObject o : toRemoveObjects)
				this.removeNetworkObject(o);
			if (updateCount >= REGULAR_UPDATE_FREQ)
				updateCount = 1;
			else
				updateCount++;
		}
	}

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    public synchronized static NetworkObjectManager getSingleton() {
        return singleton;
    }

}
