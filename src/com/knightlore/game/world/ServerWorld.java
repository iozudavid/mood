package com.knightlore.game.world;

import com.knightlore.ai.AIManager;
import com.knightlore.ai.TurretServer;
import com.knightlore.ai.TurretShared;
import com.knightlore.game.Player;
import com.knightlore.game.PlayerManager;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Zombie;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.utils.Vector2D;

public class ServerWorld extends GameWorld {

    private PlayerManager playerManager;
    private AIManager aiManager;

    public ServerWorld() {
        super();
        this.playerManager = new PlayerManager();
        this.aiManager = new AIManager(map);
    }

    public void populateWorld() {
        // add the mobs
        ShotgunPickup shot = new ShotgunPickup(new Vector2D(8, 8));
        shot.init();
        ents.add(shot);
        Zombie zom = new Zombie(new Vector2D(4, 5));
        zom.init();
        ents.add(zom);
        // add pickups
        for (int i = 5; i < 9; i += 2) {
            ShotgunPickup shotI = new ShotgunPickup(new Vector2D(i, 3));
            shotI.init();
            ents.add(shotI);
        }
        TurretShared tboi = new TurretServer(3, map.getRandomSpawnPoint(), Vector2D.UP);
        tboi.init();
    }

    @Override
    public void update() {
        for (Player player : playerManager.getPlayers()) {
            for (Entity ent : ents) {
                if (player.getBoundingRectangle().intersects(ent.getBoundingRectangle())) {
                    ent.onCollide(player);
                }
            }
        }
    }

    public Player createPlayer() {
        Vector2D pos = map.getRandomSpawnPoint();
        Player player = new Player(pos, Vector2D.UP);
        player.init();
        // ents.add(player);
        playerManager.addPlayer(player);
        return player;
    }

}
