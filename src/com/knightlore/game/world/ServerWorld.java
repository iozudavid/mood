package com.knightlore.game.world;

import java.util.List;

import com.knightlore.ai.BotInput;
import com.knightlore.ai.TurretServer;
import com.knightlore.ai.TurretShared;
import com.knightlore.game.FFAGame;
import com.knightlore.game.Player;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.SpectatorCamera;
import com.knightlore.game.entity.pickup.ShotgunPickup;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.physics.RaycastHitType;
import com.knightlore.utils.Vector2D;

public class ServerWorld extends GameWorld {

    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        gameManager = new FFAGame();
        gameManager.init();
        buildEntities();
    }

    public void buildEntities() {
        
        // add the mobs
        //ZombieServer zom = new ZombieServer(map.getRandomSpawnPoint());
        //zom.init();
        //ents.add(zom);
        //TurretShared tboi = new TurretServer(3, map.getRandomSpawnPoint(), Vector2D.UP);
        //tboi.init();
        for (int i = 0; i < 0; i++) {
            Player botPlayer = new Player(map.getRandomSpawnPoint(), Vector2D.UP);
            botPlayer.setInputModule(new BotInput());
            botPlayer.init();
            botPlayer.setName("bot" + i);
            playerManager.addPlayer(botPlayer);
        }

        SpectatorCamera cam = new SpectatorCamera(new Vector2D(10, 20), Vector2D.UP);
        cam.init();
        ents.add(cam);
    }

    @Override
    public void update() {
        super.update();
        for (Player player : playerManager.getPlayers()) {
            for (Entity ent : ents) {
                if (player.getBoundingRectangle().intersects(ent.getBoundingRectangle())) {
                    ent.onCollide(player);
                }
            }
        }
    }

    @Override
    /**
     * Casts a ray against all world, entities and players. Returns a structure
     * holding information about what was hit
     */
    public RaycastHit raycast(Vector2D pos, Vector2D direction, int segments, double maxDist, Entity ignore) {
        if (segments <= 0) {
            System.err.println("can't raycast with <= 0 segments");
            return null;
        }

        Vector2D step = Vector2D.mul(direction.normalised(), maxDist / segments);

        Vector2D p = pos;
        int x, y;

        for (int i = 0; i < segments; i++) {
            x = (int) p.getX();
            y = (int) p.getY();
            if (map.getTile(x, y).blockLOS()) {
                return new RaycastHit(RaycastHitType.WALL, p, null);
            }

            double sqrDist;
            double sqrSize;

            // cast against players
            List<Player> playerList = playerManager.getPlayers();
            for (int n = 0; n < playerList.size(); n++) {
                if (playerList.get(n) == ignore) {
                    continue;
                }
                sqrSize = playerList.get(n).getSize() * playerList.get(n).getSize();
                sqrDist = playerList.get(n).getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(RaycastHitType.PLAYER, p, playerList.get(n));
                }
            }

            // now against entities
            for (int n = 0; n < ents.size(); n++) {
                if (ents.get(n) == ignore) {
                    continue;
                }
                sqrSize = ents.get(n).getSize() * ents.get(n).getSize();
                sqrDist = ents.get(n).getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(RaycastHitType.ENTITY, p, ents.get(n));
                }
            }
            // FIXME? for some reason bounding rectangles don't work properly?
            // i can't visualise them to help debug the ray...
            // they should collide, but don't.
            // i think it's due to the fact that these are GUI rectangles. not
            // actual squares.
            // the x,y is in one corner, with a width and a height for the other
            // corner
            // this means that the bounding rectangle isn't centered on the
            // PLAYER
            /*
             * for (int n = 0; n < ents.size(); n++) { if
             * (Physics.pointInRectangleDoubleTest(p,
             * ents.get(n).getBoundingRectangle())) { return new
             * RaycastHit(RaycastHitType.ENTITY, p, ents.get(n)); } }
             * 
             * List<Player> playerList = playerManager.getPlayers(); for(int
             * n=0;n<playerList.size();n++) { if
             * (Physics.pointInRectangleDoubleTest(p,
             * playerList.get(n).getBoundingRectangle())) { return new
             * RaycastHit(RaycastHitType.ENTITY, p, playerList.get(n)); } }
             */
            p = p.add(step);
        }

        return new RaycastHit(RaycastHitType.NOTHING, Vector2D.ZERO, null);
    }

    public Player createPlayer() {
        Vector2D pos = map.getRandomSpawnPoint();
        Player player = new Player(pos, Vector2D.UP);
        player.init();
        // ents.add(PLAYER);
        playerManager.addPlayer(player);
        return player;
    }
}
