package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.knightlore.network.INetworkable;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Tuple;

public class ServerNetworkObjectManager extends NetworkObjectManager
        implements Runnable {
    // How often to send an update of state, rather than just if the
    // states have changed. A value of 100 means that in every 100 loops,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;
    // Maps network objects to their most recent state. Used to check if the new
    // state has changed (if it hasn't, don't resend it).
    private Map<UUID, Tuple<INetworkable, ByteBuffer>> networkObjects = new HashMap<>();
    private List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();
    // Counter for REGULAR_UPDATE_FREQ
    private int updateCount = 1;    
    

    @Override
    public synchronized void registerNetworkObject(INetworkable obj) {
        System.out.println("server registering net obj");
        this.networkObjects.put(obj.getObjectId(),
                new Tuple<>(obj, obj.serialize()));

    }

    // call this function to disconnect client
    // with the given id
    // onUpdate will do the job
    public void disconnectClient(UUID uuid) {
        // TODO
    }

    public synchronized void removeNetworkObject(INetworkable obj) {
        networkObjects.remove(obj.getObjectId());
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
        new Thread(this).start();
    }

    @Override
    public synchronized void onUpdate() {
        syncStates();
    }

    private synchronized void syncStates() {
        // Iterate through each networkable object. If its state
        // has changed, send the new state to each client.
        for (Entry<UUID, Tuple<INetworkable, ByteBuffer>> t : networkObjects
                .entrySet()) {
            ByteBuffer newState = t.getValue().x.serialize();
            // Send state either if we're due a regular update, or if the
            // state has changed.
            synchronized (this.clientSenders) {
                if (updateCount >= REGULAR_UPDATE_FREQ
                        || !newState.equals(t.getValue().y)) {
                    for (SendToClient s : clientSenders)
                        s.send(newState);
                    t.getValue().y = newState;
                }
            }
        }
        if (updateCount >= REGULAR_UPDATE_FREQ)
            updateCount = 1;
        else
            updateCount++;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        ByteBuffer buf;
        while (true) {
            try {
                buf = this.messages.take();
            } catch (InterruptedException e) {
                System.err.println(
                        "Error while waiting for a message from the queue.");
                e.printStackTrace();
                return;
            }
            // Get the UUID of the object referenced in the message.
            String uuidString = NetworkUtils.getStringFromBuf(buf);
            UUID uuid = UUID.fromString(uuidString);

            INetworkable obj = getNetworkObject(uuid);
            // Get the name of the method to be called on the object.
            String method = NetworkUtils.getStringFromBuf(buf);
            // Get the method itself.
            Consumer<ByteBuffer> c = obj.getNetworkConsumers().get(method);
            // The rest of the ByteBuffer is input for the method; call it.
            c.accept(buf);
        }
    }

    private synchronized INetworkable getNetworkObject(UUID uuid) {
        if (networkObjects.containsKey(uuid))
            return networkObjects.get(uuid).x;
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    }

}
