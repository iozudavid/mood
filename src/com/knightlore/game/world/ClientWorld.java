package com.knightlore.game.world;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.Environment;

public class ClientWorld extends GameWorld {

    public ClientWorld() {
        super();
    }

    public void addEntity(Entity ent) {
        this.ents.add(ent);
    }

    public void removeEntity(Entity ent) {
        this.ents.remove(ent);
    }

    public Environment getEnvironment() {
        return Environment.LIGHT_OUTDOORS;
    }

}
