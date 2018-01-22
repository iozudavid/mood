package com.knightlore.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

/*
 * Basic network connection
 * start using tcp for now, adpat it in the future to use udp
 */

public class TCPConnection extends Connection {
	
	private InputStream infoReceive;
	private OutputStream infoSend;
	private Socket socket;

	public TCPConnection(Queue<Command> commandQueue, Socket socketConnection) {
		super(commandQueue);
		this.socket = socketConnection;
		try {
			this.infoReceive = socketConnection.getInputStream();
			this.infoSend = socketConnection.getOutputStream();
		} catch (IOException e) {
			System.err.println("The connection doesn't seem to work...");
			System.exit(1);
		}
	}

	@Override
	public void send(byte[] data) {
		try {
			infoSend.write(data);
		} catch (IOException e) {
			System.err.println("Communication broke...");
			System.exit(1);
		}

	}

	@Override
	public byte[] receive() {
		byte[] data = null;
		try {
			int i, count=0;
			data = new byte[256];
			while((i = infoReceive.read())!=-1) {
				data[count] = (byte) i;
				count++;
	         }
			return data;
		} catch (IOException e) {
			System.err.println("Communication broke...");
			System.exit(1);
		}
		return null;
	}

}
