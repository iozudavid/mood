package com.knightlore.gamelogic;

import java.util.ArrayList;
import java.util.List;

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
        mobs.removeIf(m -> !m.exists());
    }

    @Override
    public void onDestroy() {

    }

}
