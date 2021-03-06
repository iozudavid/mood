package com.knightlore.network.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

/**
 * Class used to predict the next client state for current user.
 * As well as server reconciliation.
 *
 * @author David Iozu
 */
public class Prediction {

    // first double is the input timestemp
    private final LinkedHashMap<Double, byte[]> clientInputHistory;
    private final double maxTolerance = 0.5D;
    private final double converge = 0.35D;
    private Player nextPrediction;
    private ByteBuffer lastReceivedFromServer;

    public Prediction() {
        this.clientInputHistory = new LinkedHashMap<>();
        this.nextPrediction = null;
        this.lastReceivedFromServer = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
    }

    /**
     * Called when a packet is receiving from the server
     * containing informations about current player.
     * Using this as a start point, apply all player's inputs which were performed
     * after this packet was sent. This will generate the next prediction.
     *
     * @param player   - current player
     * @param received - ByteBuffer received from server
     */
    public void onServerFrame(Player player, ByteBuffer received) {
        // remove the old history
        // inputs before this packet was sent
        received.position(0);
        NetworkUtils.getStringFromBuf(received);
        NetworkUtils.getStringFromBuf(received);
        double moveSpeed = received.getDouble();
        double strafeSpeed = received.getDouble();
        double rotationSpeed = received.getDouble();
        player.setMoveSpeed(moveSpeed);
        player.setStrafeSpeed(strafeSpeed);
        player.setRotateSpeed(rotationSpeed);
        // get player's stats on server
        double size = received.getDouble();
        player.setSize(size);
        double xPos = received.getDouble();
        double yPos = received.getDouble();
        double xDir = received.getDouble();
        double yDir = received.getDouble();
        // plane...
        received.getDouble();
        received.getDouble();
        String name = NetworkUtils.getStringFromBuf(received);
        player.setName(name);
        int shootOnNext = received.getInt();
        player.setOnNextShot(shootOnNext == 1);
        double timeSent = received.getDouble();
        boolean respawn = received.getInt() == 1;
        player.setRespawn(respawn);
        int currentHealth = received.getInt();
        player.setHealth(currentHealth);
        int score = received.getInt();
        player.setScore(score);
        
        WeaponType newWeaponType = WeaponType.VALUES[received.getInt()];
        player.setCurrentWeaponType(newWeaponType);
        
        player.setTeam(Team.values()[received.getInt()]);

        // remove data inserted before this packet was sent
        if (!Arrays.equals(this.lastReceivedFromServer.array(), received.array())) {
            if (this.nextPrediction == null) {
                this.nextPrediction = new Player(new Vector2D(xPos, yPos), new Vector2D(xDir, yDir));
            }
            this.clientInputHistory.entrySet().removeIf(next -> next.getKey() <= timeSent);
            // create a new player
            // which will predict the next steps

            this.nextPrediction.setxPos(xPos);
            this.nextPrediction.setyPos(yPos);
            this.nextPrediction.setxDir(xDir);
            this.nextPrediction.setyDir(yDir);
            this.nextPrediction.setOnNextShot(shootOnNext == 1);

            synchronized (this.clientInputHistory) {
                for (byte[] nextInput : this.clientInputHistory.values()) {
                    this.nextPrediction.setInputState(nextInput);
                }
            }
            // use it as a start point
            // for the next prediction
            this.lastReceivedFromServer = received;
            if (respawn) {
                player.setxPos(nextPrediction.getPosition().getX());
                player.setyPos(nextPrediction.getPosition().getY());
                player.setxDir(nextPrediction.getDirection().getX());
                player.setyDir(nextPrediction.getDirection().getY());
                player.onClientRespawn();
            }
        }

    }

    /**
     * Called when the client input commands from keyboard.
     * Takes the last prediction and use it to do server reconciliation
     * then generate new state by applying inputs to reconciliated state
     *
     * @param player       - current player
     * @param input        - all keyboard inputs
     * @param packetNumber - number of packet sent
     * @return new player state
     */
    public void update(Player player, byte[] input, double packetNumber) {
        //use last prediction based on server stats
        //to construct the new position
        if (this.nextPrediction != null) {
            if ((Math.abs(player.getxPos() - this.nextPrediction.getPosition().getX()) > this.maxTolerance
                    || Math.abs(player.getyPos() - this.nextPrediction.getPosition().getY()) > this.maxTolerance
                    || Math.abs(player.getxDir() - this.nextPrediction.getDirection().getX()) > this.maxTolerance
                    || Math.abs(player.getyDir() - this.nextPrediction.getDirection().getY()) > this.maxTolerance)) {
                if (this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)
                        || this.isMoveActivated(ClientController.LEFT, input)
                        || this.isMoveActivated(ClientController.RIGHT, input)) {
                    player.setxPos(nextPrediction.getPosition().getX());
                }
                if (this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)
                        || this.isMoveActivated(ClientController.LEFT, input)
                        || this.isMoveActivated(ClientController.RIGHT, input)) {
                    player.setyPos(nextPrediction.getPosition().getY());
                }
                if (this.isMoveActivated(ClientController.ROTATE_ANTI_CLOCKWISE, input)
                        || this.isMoveActivated(ClientController.ROTATE_CLOCKWISE, input)
                        || this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)) {
                    player.setxDir(nextPrediction.getDirection().getX());
                    player.setyDir(nextPrediction.getDirection().getY());
                }
            } else {
                if (this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)
                        || this.isMoveActivated(ClientController.LEFT, input)
                        || this.isMoveActivated(ClientController.RIGHT, input)) {
                    player.setxPos(smooth(player.getxPos(), this.nextPrediction.getPosition().getX()));
                }
                if (this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)
                        || this.isMoveActivated(ClientController.LEFT, input)
                        || this.isMoveActivated(ClientController.RIGHT, input)) {
                    player.setyPos(smooth(player.getyPos(), this.nextPrediction.getPosition().getY()));
                }
                if (this.isMoveActivated(ClientController.ROTATE_ANTI_CLOCKWISE, input)
                        || this.isMoveActivated(ClientController.ROTATE_CLOCKWISE, input)
                        || this.isMoveActivated(ClientController.FORWARD, input)
                        || this.isMoveActivated(ClientController.BACKWARD, input)) {
                    player.setxDir(smooth(player.getxDir(), this.nextPrediction.getDirection().getX()));
                    player.setyDir(smooth(player.getyDir(), this.nextPrediction.getDirection().getY()));
                }
            }
        }

        player.setInputState(input);
        synchronized (this.clientInputHistory) {
            this.clientInputHistory.put(packetNumber, input);
        }
    }

    /**
     * Called when local machine needs to reconciliate with server.
     *
     * @param playerState    - player state on local machine
     * @param predictedState - player state predicted using server state
     * @return reconciled state
     */
    public double smooth(double playerState, double predictedState) {
        double delta = playerState - predictedState;
        return playerState - delta * this.converge;
    }

    /**
     * Checks if a given move is performed.
     *
     * @param c     - move to be checked
     * @param input - set of inputs from user
     * @return whether that move is performed or not
     */
    public boolean isMoveActivated(ClientController c, byte[] input) {
        for (int i = 0; i < ClientProtocol.getIndexActionMap().size(); i = i + 2) {
            try {
                if (c == ClientProtocol.getByIndex(i)) {
                    return input[i + 1] == 1;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

}
