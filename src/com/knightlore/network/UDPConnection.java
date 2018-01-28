package com.knightlore.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a connection from the server to a particular client, identified by
 * a UUID.
 * 
 * @author Will
 */

public class UDPConnection extends Connection {

    // The time-stamp of when the most recent communication (packet) was
    // received.
    private Date lastPacketDate;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] data;
    private InetAddress address;

    public UDPConnection(InetAddress address, UUID clientUUID) {
        super(clientUUID);

        lastPacketDate = new Date(System.currentTimeMillis());
        this.address = address;
        try {
            this.socket = new DatagramSocket(Port.number);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private boolean verify(byte[] data) {
        // verify the ip from receciving packet
        if (data == null || data.length == 0)
            return false;
        return true;
    }

    /**
     * Send data to the other party.
     * 
     * @param data
     */
    public void send(byte[] data) {
        // TODO
        try {
            this.packet = new DatagramPacket(data, data.length, this.address,
                    Port.number);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive data from the other party. This is a blocking operation, so the
     * method will not return until a packet is received.
     */
    public byte[] receiveBlocking() {
        try {
            this.data = new byte[256];
            while (!this.verify(this.data)) {
                this.packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
            }
            this.lastPacketDate = new Date(System.currentTimeMillis());
            return packet.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getLastPacketDate() {
        return lastPacketDate;
    }

}
