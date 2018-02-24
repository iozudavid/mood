package com.knightlore.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.knightlore.engine.GameObject;
import com.knightlore.game.entity.Entity;

public class MobManager extends GameObject {

    private List<Entity> mobs;

    public MobManager() {
        mobs = new ArrayList<Entity>();
    }

    public void addMob(Entity m) {
        mobs.add(m);
    }

    public void delMob(Entity m) {
        mobs.remove(m);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onUpdate() {
        ListIterator<Entity> itr = mobs.listIterator();
        while (itr.hasNext()) {
            Entity m = itr.next();
            if (!m.exists())
                itr.remove();
        }
    }

    @Override
    public void onDestroy() {

    }

}
