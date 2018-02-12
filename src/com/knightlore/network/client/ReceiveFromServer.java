package com.knightlore.network.client;

import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.Renderer;
import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.render.Camera;
import com.knightlore.utils.Vector2D;

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
            Renderer rend = GameEngine.getSingleton().getRenderer();
            Player player = new Player(this.clientID, new Camera(0, 0, 0, 0, 0,
                    0, rend.getMap()));
            player.deserialize(serverCommand);
            // FIXME: interface better with World to provide the camera during
            // its constructor.
            rend.setCamera(player.getCamera());

            while (!conn.terminated && (packet = conn.receive()) != null) {
                ServerCommand command = ServerCommand.decodePacket(packet);
                
                //if we receive the disconnect packet
                //then remove object from game engine
                //and from network manager
                if(ServerProtocol.isDisconnectedState(packet)){
                    NetworkObjectManager.getSingleton().getNetworkObject(command.getObjectId()).destroy();
					NetworkObjectManager.getSingleton().disconnectClient(command.getObjectId());
					rend.updateNetworkObjectPos(
							NetworkObjectManager.getSingleton().getNetworkObject(command.getObjectId()), null, null);
                    continue;
                }
                
                // Update the object with the received information.
                NetworkObjectManager.getSingleton()
                        .getNetworkObject(command.getObjectId())
                        .deserialize(command);
                
                rend.updateNetworkObjectPos(
                        NetworkObjectManager.getSingleton()
                                .getNetworkObject(command.getObjectId()),
                        new Vector2D(
                                command.getValueByControl(ServerControl.XPOS),
                                command.getValueByControl(ServerControl.YPOS)),
                        new Vector2D(
                        		command.getValueByControl(ServerControl.XDIR),
                        		command.getValueByControl(ServerControl.YDIR)));

            }
            if (!conn.terminated)
                System.out.println("Got null packet, exiting");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
