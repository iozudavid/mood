package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;

/**
 * Abstract network manager to be implemented on both client and server sides.
 * Handle to deserialize correctly a new packet received by networking.
 * 
 * @author David Iozu, Will Miller
 *
 */
public abstract class NetworkObjectManager implements INetworkable, Runnable {
    // This is a special UUID that refers to the NetworkObjectManager itself.
    public static final UUID MANAGER_UUID = UUID
            .fromString("00000000-0000-0000-0000-000000000000");
    protected final Map<String, Consumer<ByteBuffer>> networkConsumers = new HashMap<>();

    private final BlockingQueue<ByteBuffer> messages = new LinkedBlockingQueue<>();

    public void processMessage(ByteBuffer buffer) {
        this.messages.add(buffer);
    }

    public abstract void registerNetworkObject(NetworkObject obj);

    public abstract void removeNetworkObject(NetworkObject obj);

    public abstract NetworkObject getNetworkObject(UUID uuid);

    @Override
    public Map<String, Consumer<ByteBuffer>> getNetworkConsumers() {
        return networkConsumers;
    }

    @Override
    public synchronized ByteBuffer serialize() {
        // N.B. The manager should never be serialised...
        return null;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buffer) {
        // N.B. The manager should never be serialised...
    }

    /**
     * @return manager unique UUID
     */
    @Override
    public UUID getObjectId() {
        return MANAGER_UUID;
    }

    protected void init() {
        System.out.println("init called");
        new Thread(this).start();
        System.out.println("Object manager started");
    }

    /**
     * This is used by both client and server sides. A new packet is waited to
     * arrive. Then it's depacked up to the method name to be called. When we
     * know the class and the method we need to call, just perform the call and
     * pass the packet by.
     * For user prediction, packet containing player states it's not deserialize,
     * it's used to predict or correct user states.
     */
    @Override
    public void run() {
    	int i=0;
        while (true) {
            ByteBuffer buf;
            try {
                buf = this.messages.take();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for a message.");
                e.printStackTrace();
                return;
            }
            buf.position(0);
            UUID objID;
            try {
                // HACK
                objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
            } catch (IllegalArgumentException e) {
                // An illegal UUID probably indicates a malformed packet, so ignore it.
                continue;
            }
            String methodName = NetworkUtils.getStringFromBuf(buf);
            Consumer<ByteBuffer> cons;
            if (objID.equals(MANAGER_UUID)) {
                // This message is directed at the NetworkManager, i.e. ourself.
                cons = this.getNetworkConsumers().get(methodName);
            } else {
                NetworkObject obj = this.getNetworkObject(objID);
                if(obj == null) {
                    System.err.println("Received data for unknown id "+objID);
                    System.err.println("ignoring this packet ^ ");
                    continue;
                }
                cons = obj.getNetworkConsumers().get(methodName);
                if (this instanceof ClientNetworkObjectManager) {
                    ClientNetworkObjectManager netManager = (ClientNetworkObjectManager)this;
                    assert(netManager.getMyPlayer() != null);
                	if (((ClientNetworkObjectManager)this).getMyPlayer()!= null &&
                			((ClientNetworkObjectManager)this).getMyPlayer().getObjectId().equals(objID) &&
                				methodName.equals("deserialize")) {
                		if(((ClientNetworkObjectManager)this).hasFinishedSetup() ){
                			if(i==1) {
                			buf.rewind();
                			((ClientNetworkObjectManager)this).addToPlayerStateOnServer(buf);
                			continue;
                			} else {
                                i++;
                            }
                		}
                	}
                }
            }
            // Execute the specified method on the specified object, with the
            // rest of the ByteBuffer as input.
          	cons.accept(buf);
        }
    }

}
