package com.knightlore.ai;

import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.utils.Physics;
import com.knightlore.utils.Vector2D;

public final class TurretServer extends TurretShared {

    public TurretServer(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onUpdate() {
        // 60 ticks per second
        long currentTime = GameEngine.ticker.getTime();
        aim();
        if (currentTime >= nextCheckTime) {
            think();
            shoot();
            nextCheckTime = currentTime + TURRET_CHECK_DELAY;
        }
    }
    
    @Override
    public void onCollide(Player ent) {
    }

    private void think() {
        List<Player> players = GameEngine.getSingleton().getGameObjectManager().findObjectsOfType(Player.class);
        for (Player player : players) {
            double sqrDist = player.getPosition().sqrDistTo(this.getPosition());
            if (sqrDist < sqrRange) {
                target = player;
                return;
            }
        }
        target = null;

    }

    protected void aim() {
        long currentTime = GameEngine.ticker.getTime();
        if (!hasTarget()) {
            this.direction = new Vector2D(Math.sin(currentTime / 90d), Math.cos(currentTime / 90d));
            this.plane = this.direction.perpendicular();
            return;
        }
        // we got a target, let's look at them
        this.direction = target.getPosition().subtract(this.getPosition()).normalised();
        this.plane = direction.perpendicular();

    }

    @Override
    protected void shoot() {
        if (target == null) {
            return;
        }
        // can't shoot if there's something in the way
        if (Physics.linecastQuick(this.position, target.getPosition(), 50)) {
            return;
        }
        System.out.println("!!! BANG !!!");
        System.out.println("A player just got shot by a turret.");
    }

    @Override
    protected boolean hasTarget() {
        return target != null;
    }
}
