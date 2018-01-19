package com.knightlore.network;

import java.util.UUID;

public class Client {
    private Connection conn;
    private UUID uuid;

    public Client(UUID uuid, Connection conn) {
        this.conn = conn;
        this.uuid = uuid;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
}
