package com.knightlore.network.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.gui.GameChat;
import com.knightlore.gui.TextArea;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.Camera;

/**
 * Manager on the client side. Updates other game objects states to keep the
 * client in touch with the game
 * 
 * @author David Iozu, Will Miller
 *
 */
public class ClientNetworkObjectManager extends NetworkObjectManager {
    private final Map<UUID, NetworkObject> networkObjects = new HashMap<>();
    private Player myPlayer = null;
    // Whether we've received enough information to start the game.
    private boolean finishedSetUp = false;
    private ArrayList<ByteBuffer> myPlayerStateOnServer;

    private ClientWorld clientWorld;

    private BlockingQueue<ByteBuffer> teamChat;
    private SendToServer sendToServer;

    public ClientNetworkObjectManager(ClientWorld world) {
        super();
        this.clientWorld = world;
        this.myPlayerStateOnServer = new ArrayList<>();
        this.teamChat = new LinkedBlockingQueue<>();
        setNetworkConsumers();
    }

    /**
     * Set all methods to be called remotely.
     */
    private void setNetworkConsumers() {
        networkConsumers.put("registerPlayerIdentity",
                this::registerPlayerIdentity);
        networkConsumers.put("newObjCreated", this::newObjCreated);
        networkConsumers.put("objDestroyed", this::objDestroyed);
        networkConsumers.put("receiveMapSeed", this::receiveMapSeed);
        networkConsumers.put("receiveReadySignal", this::receiveReadySignal);
        networkConsumers.put("displayMessage", this::displayMessage);
    }

    /**
     * Called remotely when new a network object has connected. Store it to
     * client's game.
     * 
     * @param obj
     *            - network object to be registered
     */
    @Override
    public synchronized void registerNetworkObject(NetworkObject obj) {
        System.out.println("client registering net obj " + obj.getObjectId());
        this.networkObjects.put(obj.getObjectId(), obj);
    }

    /**
     * Removes a network object from client's game.
     * 
     * @param obj
     *            - network object to be removed
     */
    @Override
    public synchronized void removeNetworkObject(NetworkObject obj) {
        networkObjects.remove(obj.getObjectId());
    }

    /**
     * 
     * @return current player
     */
    public Player getMyPlayer() {
        return myPlayer;
    }

    /**
     * Receives a message via networking and add it to chat GUI to be displayed.
     * 
     * @param b
     *            - ByteBuffer containing message to be displayed
     */
    private synchronized void displayMessage(ByteBuffer b) {
        // can't put a message if the engine isn't done...
        // silly network
        if (!GameEngine.getSingleton().doneInit()) {
            return;
        }
        String message = NetworkUtils.getStringFromBuf(b);
        assert (message != null);
        GameEngine g = GameEngine.getSingleton();
        ClientWorld world = (ClientWorld) g.getWorld();
        GameChat c = world.getGameChat();
        if (c == null)
            return;
        TextArea t = c.getTextArea();
        t.addText(message);

    }

    /**
     * Called remotely when a new network object is created on the server.
     * 
     * @param buf
     *            - buffer containing serialization of the new object
     */
    @SuppressWarnings("unchecked")
    private synchronized void newObjCreated(ByteBuffer buf) {
        System.out.println("Receiving new object details from server");
        String className = NetworkUtils.getStringFromBuf(buf);
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        if (networkObjects.containsKey(objID))
        // We already know about this object.
        {
            return;
        }
        try {
            Class<Entity> cls = (Class<Entity>) Class.forName(className);
            // Build the new object.
            Method method = cls.getMethod("build", UUID.class,
                    ByteBuffer.class);
            // Static method, so pass null for object reference. The remainder
            // of the ByteBuffer constitutes
            // the state of the object.
            NetworkObject obj = (NetworkObject) method.invoke(null, objID, buf);
            // Entities need to exist in the world.
            if (obj instanceof Entity) {
                clientWorld.addEntity((Entity) obj);
            }
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            System.err.println(
                    "Error when attempting to call the static method build(UUID, ByteBuffer) on the class "
                            + className
                            + ". Are you sure you implemented it? See NetworkObject for details.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("The specified class " + className
                    + " could not be found.");
            e.printStackTrace();
        }
    }

    /**
     * Called remotely when an object is destroyed. Delete it from local game.
     * 
     * @param buf
     *            ByteBuffer sent via networking containing details about object
     *            to be destroyed
     * 
     */
    private synchronized void objDestroyed(ByteBuffer buf) {
        System.out.println("Receiving object deletion message from server");
        UUID objID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        NetworkObject toBeDestroyedObject = this.getNetworkObject(objID);
        toBeDestroyedObject.destroy();
        if (toBeDestroyedObject instanceof Entity) {
            clientWorld.removeEntity((Entity) toBeDestroyedObject);
            GameEngine g = GameEngine.getSingleton();
            ClientWorld world = (ClientWorld) g.getWorld();
            GameChat c = world.getGameChat();
        }
    }

    /**
     * Called remotely when map seed is generated Set received map.
     * 
     * @param buf
     *            - ByteBuffer containing map seed information
     */
    private synchronized void receiveMapSeed(ByteBuffer buf) {
        long seed = buf.getLong();
        System.out.println("received map seed " + seed);
        clientWorld.setUpWorld(seed);
    }

    /**
     * Called remotely when we receive a message from the server to tell us what
     * our UUID is. Set the received UUID to current player.
     * 
     * @param buf
     *            - ByteBuffer to be processed containing UUID information
     */
    private synchronized void registerPlayerIdentity(ByteBuffer buf) {
        System.out.println("Registering player identity");
        UUID myPlayerUUID = UUID.fromString(NetworkUtils.getStringFromBuf(buf));
        myPlayer = (Player) networkObjects.get(myPlayerUUID);
        System.out.println("Created and set player.");
    }

    /**
     * @param uuid
     *            - object's UUID which is required
     * @return the network object which has the given UUID if any
     */
    @Override
    public synchronized NetworkObject getNetworkObject(UUID uuid) {
        synchronized (this.networkObjects) {
            if (networkObjects.containsKey(uuid)) {
                return networkObjects.get(uuid);
            }
        }
        System.err.println("No network object with UUID " + uuid
                + " could be found on this client.");
        return null;
    }

    /**
     * Send to the server what we want our name to be.
     * 
     * @param buf
     *            - ByteBuffer containing desired name
     */
    private synchronized void receiveReadySignal(ByteBuffer buf) {
        System.out.println("Sending name");
        sendName();
        Camera camera = new Camera(clientWorld.getMap());
        camera.setSubject(myPlayer);
        GameEngine.getSingleton().setCamera(camera);
        // We can now start the game.
        this.finishedSetUp = true;
    }

    /**
     * Wait for all information to be received - e.g., map seed, object details,
     * player identity.
     * 
     * @param serverSender
     *            - set the server sender for our player in the manager
     */
    public void init(SendToServer serverSender) {
        super.init();

        this.sendToServer = serverSender;
        while (!finishedSetUp) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println(
                        "Unexpected interruption while waiting for game to be ready."
                                + e.getMessage());
            }
        }
    }

    /**
     * Let the server knows what our player's name is.
     */
    private void sendName() {
        ByteBuffer buf = ByteBuffer.allocate(BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(buf, MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "receiveName");
        // Let the server know who we are.
        NetworkUtils.putStringIntoBuf(buf,
                this.myPlayer.getObjectId().toString());
        NetworkUtils.putStringIntoBuf(buf, GameSettings.playerName);
        this.sendToServer.send(buf);
    }

    /**
     * 
     * @return whether the setup has finished or not
     */
    public boolean hasFinishedSetup() {
        return finishedSetUp;
    }

    /**
     * Called remotely to add new message to the game chat.
     * 
     * @param message
     *            - ByteBuffer containing a message came from server
     */
    public void addToChat(ByteBuffer message) {
        this.teamChat.offer(message);
    }

    /**
     * Takes next message if any from current player and send it to server.
     * 
     * @return ByteBuffer to be sent to the server and then forwarded to players
     */
    public ByteBuffer takeNextMessageToSend() {
        try {
            if (this.teamChat.isEmpty()) {
                return null;
            }
            return this.teamChat.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * @param b
     *            the ByteBuffer containing the player's state on the server
     *            which will be used by client prediction to reconciliate with
     *            the server
     */
    public void addToPlayerStateOnServer(ByteBuffer b) {
        synchronized (this.myPlayerStateOnServer) {
            this.myPlayerStateOnServer.add(b);
        }
    }

    /**
     * @return a list of all new states received from the server
     */
    public ArrayList<ByteBuffer> getPlayerStateOnServer() {
        synchronized (this.myPlayerStateOnServer) {
            ArrayList<ByteBuffer> copyStates = new ArrayList<>(
                    this.myPlayerStateOnServer);
            this.myPlayerStateOnServer.clear();
            return copyStates;
        }
    }

}
