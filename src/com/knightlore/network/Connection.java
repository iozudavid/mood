package com.knightlore.network;

import java.util.Date;

public class Connection {
    private String uuid;
    private Date lastComm;
    
    public Connection(String uuid) {
        this.uuid = uuid;
    }
    
    public void send(byte[] data) {
        
    }
    
    public byte[] receive() {
        return null;
    }
}
