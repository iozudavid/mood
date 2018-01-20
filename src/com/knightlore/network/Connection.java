package com.knightlore.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Connection extends Thread{
	
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] data;
    private InetAddress address;
    private long lastReceiving;
    private final Object lock;
    private Thread timeoutThread;
    
    public Connection(InetAddress address) {
    	lastReceiving = System.currentTimeMillis();
    	lock          = new Object();
    	this.address  = address;
        this.data     = new byte[256];
        try {
			this.socket = new DatagramSocket(Port.number);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        timeoutThread = new Thread() {
        	
        	private boolean runningAnnonymous = true;
            
        	public void run() {
                while(runningAnnonymous){
                	long currentTime = System.currentTimeMillis();
                	if(currentTime - getLastReceivingTime() >= 5000){
                		String message = "You have been disconnected.";
                		send(message.getBytes());
                		runningAnnonymous = false;
                	}
                }
                
            }

        };
        timeoutThread.start();
    }
    
    private void setLastReceivingTime(long newReceivingTime){
    	synchronized(lock){
    		this.lastReceiving = newReceivingTime;
    	}
    }
    
    private long getLastReceivingTime(){
    	synchronized(lock){
    		return this.lastReceiving;
    	}
    }
    
    private boolean verify(byte[] data){
    	//verify the ip from receciving packet
    	return true;
    }
    
    public void send(byte[] data) {
    	try {
    		this.packet = new DatagramPacket(data, data.length, this.address, Port.number);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public byte[] receive() {
       try {
    	   this.packet = new DatagramPacket(data, data.length);
    	   socket.receive(packet);
    	   this.setLastReceivingTime(System.currentTimeMillis()); 
    	   return packet.getData();
       	} catch (IOException e) {
       		e.printStackTrace();
       	}
       	return null;
    }
    
    public boolean isStillAlive(){
    	return timeoutThread.isAlive();
    }
   
}
