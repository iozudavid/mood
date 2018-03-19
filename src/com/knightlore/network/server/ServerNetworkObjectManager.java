package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
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
    private final Map<UUID, Tuple<NetworkObject, ByteBuffer>> networkObjects = new HashMap<>();
    private final List<SendToClient> clientSenders = new CopyOnWriteArrayList<>();
    // Counter for REGULAR_UPDATE_FREQ
    private int updateCount = 1;

    private ServerWorld serverWorld;

    public ServerNetworkObjectManager(ServerWorld world) {
        super();
        this.serverWorld = world;

        setNetworkConsumers();
    }

    private void setNetworkConsumers() {
        this.networkConsumers.put("receiveName", this::receiveName);
    }

    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
        UUID uuid = obj.getObjectId();
        this.networkObjects.put(uuid, new Tuple<>(obj, obj.serialize()));
        // Notify all clients of the newly-created object.
        this.sendToClients(getObjectCreationMessage(obj));
    }

    private ByteBuffer getObjectCreationMessage(NetworkObject obj) {
        // Send the current state when sending the creation message.
        ByteBuffer state = networkObjects.get(obj.getObjectId()).y;
        // Leave room for the state and the additional overhead as well.
        ByteBuffer buf = ByteBuffer
                .allocate(2 * BYTE_BUFFER_DEFAULT_SIZE);
        // Send the message to the ClientNetworkObjectManager.
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        // The remote method to call.
        NetworkUtils.putStringIntoBuf(buf, "newObjCreated");
        // Let the client know which class it needs to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getClientClassName());
        // UUID of object to instantiate.
        NetworkUtils.putStringIntoBuf(buf, obj.getObjectId().toString());
        final ByteBuffer readOnlyState = state.asReadOnlyBuffer();
        buf.put(readOnlyState);
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
        System.out.println("Removing " + obj.getObjectId());
        UUID uuid = obj.getObjectId();
        // Notify all clients of the destroyed object.
        this.sendToClients(getObjectDestroyMessage(obj));
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
            ByteBuffer newState = t.getValue().x.serialize();
            // Send state either if we're due a regular update, or if the
            // state has changed.
            synchronized (this.clientSenders) {
                // check if they are equal
                // by transforming into arrays
                // and use the standard library
                if (updateCount >= REGULAR_UPDATE_FREQ || !Arrays
                        .equals(newState.array(), t.getValue().y.array())) {
                    this.sendToClients(newState);
                    // networkObjects.put(t.getKey(), new
                    // Tuple<>(t.getValue().x, newState));
                    t.getValue().y = newState;
                }

                if (getNetworkObject(t.getKey()) instanceof Entity) {
                    Entity e = (Entity) getNetworkObject(t.getKey());
                    Optional<ByteBuffer> systemMessages = e.getSystemMessages();
                    systemMessages.ifPresent(this::sendToClients);
                }

                if (getNetworkObject(t.getKey()) instanceof Player) {
                    Player p = (Player) getNetworkObject(t.getKey());
                    Optional<ByteBuffer> toTeam = p.getTeamMessages();
                    Predicate<Entity> predicate = (e) -> e.team == p.team;
                    toTeam.ifPresent(byteBuffer -> this
                            .sendToClientsIf(byteBuffer, predicate));

                    Optional<ByteBuffer> toAll = p.getAllMessages();
                    toAll.ifPresent(this::sendToClients);
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
    private void sendToClients(ByteBuffer buf) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders) {
                s.send(buf);
            }
        }
    }

    /**
     * Receive a player's requested name.
     * 
     * @param buf:
     *            The message received from the client.
     */
    private void receiveName(ByteBuffer buf) {
        System.out.println("Receiving player name");
        UUID playerID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        Player player = (Player) GameEngine.getSingleton()
                .getNetworkObjectManager().getNetworkObject(playerID);
        player.setName(NetworkUtils.getStringFromBuf(buf));
        System.out.println("Name is now " + player.getName());
    }

    private void sendToClientsIf(ByteBuffer buf, Predicate<Entity> pred) {
        synchronized (clientSenders) {
            for (SendToClient s : clientSenders) {
                if (pred.test((Entity) getNetworkObject(s.getUUID())))
                    s.send(buf);
            }
        }
    }

}
