package com.knightlore.network.client;

import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.render.Camera;

public class ReceiveFromServer implements Runnable {

    private Connection conn;
    private UUID clientID;

    public ReceiveFromServer(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            byte[] packet;

            // The first state sent will always represent the current player,
            // from which we can infer our own UUID.
            packet = conn.receive();
            ServerCommand serverCommand = ServerCommand.decodePacket(packet);
            this.clientID = serverCommand.getObjectId();
            // FIXME: Create Camera inside player, don't pass in dummy values to
            // initialise.
            Player player = new Player(this.clientID, new Camera(0, 0, 0, 0, 0,
                    0, GameEngine.getSingleton().getWorld().getMap()));
            player.deserialize(serverCommand);
            // FIXME: interface better with World to provide the camera during
            // its constructor.
            GameEngine.getSingleton().getWorld().setCamera(player.getCamera());

            while (!conn.terminated && (packet = conn.receive()) != null) {
                ServerCommand command = ServerCommand.decodePacket(packet);
             
                // don't know why this is here, 
                // i will keep it as a comment til i clarify with will (david)
                //   packet = conn.receive();
                
                //if we receive the disconnect packet
                //then remove object from game engine
                //and from network manager
                if(ServerProtocol.isDisconnectedState(packet)){
                    NetworkObjectManager.getSingleton().getNetworkObject(command.getObjectId()).destroy();
                    NetworkObjectManager.getSingleton().disconnectClient(command.getObjectId());
                    continue;
                }
                
                // Update the object with the received information.
                NetworkObjectManager.getSingleton()
                        .getNetworkObject(command.getObjectId())
                        .deserialize(command);
                // System.out.println("=====NEW PACKET=====");
                // System.out.println("Received time: " +
                // command.getTimeSent());
                // System.out.println("Client ID: " + command.getObjectId());
                // for (ServerControl c : ServerControl.values()) {
                // System.out.println(c + ": " + command.getValueByControl(c));
                // }
                // System.out.println("=====END OF PACKET=====");

            }
            if (!conn.terminated)
                System.out.println("Got null packet, exiting");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
