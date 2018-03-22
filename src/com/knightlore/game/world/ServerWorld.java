package com.knightlore.game.world;

import java.util.Iterator;

import com.knightlore.ai.BotInput;
import com.knightlore.game.FFAGame;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.SpectatorCamera;
import com.knightlore.game.entity.ZombieServer;
import com.knightlore.utils.Vector2D;

public class ServerWorld extends GameWorld {

    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        gameManager = new FFAGame();
        gameManager.init();
        buildEntities();
    }

    private void buildEntities() {
        for (int i = 0; i < 0; i++) {
            ZombieServer zom = new ZombieServer(map.getRandomSpawnPoint());
            zom.init();
            this.addEntity(zom);
        }

        // TurretShared tboi = new TurretServer(3, map.getRandomSpawnPoint(),
        // Vector2D.UP);
        // tboi.init();
        for (int i = 0; i < 5; i++) {
            Player botPlayer = new Player(map.getRandomSpawnPoint(),
                    Vector2D.UP);
            botPlayer.setInputModule(new BotInput());
            botPlayer.init();
            botPlayer.setName("bot" + i);
            playerManager.addPlayer(botPlayer);
        }

        SpectatorCamera cam = new SpectatorCamera(new Vector2D(10, 20),
                Vector2D.UP);
        cam.init();
        this.addEntity(cam);
    }

    @Override
    public void update() {
        super.update();
        for (Player player : playerManager.getPlayers()) {
            Iterator<Entity> it = this.getEntityIterator();
            while (it.hasNext()) {
                Entity ent = it.next();
                if (player.getBoundingRectangle()
                        .intersects(ent.getBoundingRectangle())) {
                    ent.onCollide(player);
                }
            }
        }
    }

    public Player createPlayer() {
        Vector2D pos = map.getRandomSpawnPoint();
        Player player = new Player(pos, Vector2D.UP);
        player.init();
        player.sendSystemMessage(
                "System: Player " + player.getName() + " " + " has connected.");
        playerManager.addPlayer(player);
        return player;
    }

    @Override
    public void onPostEngineInit() {

    }
}
