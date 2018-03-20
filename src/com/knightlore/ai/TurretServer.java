package com.knightlore.ai;

import java.nio.ByteBuffer;
import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.physics.Physics;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.Vector2D;

public final class TurretServer extends TurretShared {

    private static final int DAMAGE = 5;

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
    public void onCollide(Player player) {
    }

    private void think() {
        List<Player> players = GameEngine.getSingleton().getGameObjectManager().findObjectsOfType(Player.class);
        for (Entity player : players) {
            double sqrDist = player.getPosition().sqrDistTo(this.position);
            if (sqrDist < sqrRange) {
                // compute dir to check
                Vector2D checkDir = player.getPosition().subtract(this.position);
                RaycastHit hit = GameEngine.getSingleton().getWorld().raycast(position, checkDir, 50, range, this);
                // did we hit anything?
                if (hit.didHitEntity()) {
                    target = hit.getEntity();
                    return;
                }
            }
        }
        target = null;
    }

    protected void aim() {
        long currentTime = GameEngine.ticker.getTime();
        if (!hasTarget()) {
            this.direction = new Vector2D(Math.sin(currentTime / 90d), Math.cos(currentTime / 90d));
            this.plane = this.direction.perpendicular().normalised();
            return;
        }
        // we got a target, let's look at them
        this.direction = target.getPosition().subtract(this.getPosition());
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
        // just deal 15 damage
        target.takeDamage(DAMAGE, this);
    }

    @Override
    protected boolean hasTarget() {
        return target != null;
    }

}
