package com.knightlore.game.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.knightlore.ai.AIManager;
import com.knightlore.game.GameManager;
import com.knightlore.game.Player;
import com.knightlore.game.PlayerManager;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.physics.RaycastHitType;

public abstract class GameWorld {

    private static final int TEST_XSIZE = 40; // 16;
    private static final int TEST_YSIZE = 60; // 32;
    private static final long TEST_SEED = 47L; // 25L;

    protected Map map;
    protected PlayerManager playerManager;
    protected GameManager gameManager = null;
    private AIManager aiManager;
    private List<Entity> ents;

    private final ConcurrentLinkedQueue<Entity> entsToAdd = new ConcurrentLinkedQueue<Entity>();
    private final ConcurrentLinkedQueue<Entity> entsToRemove = new ConcurrentLinkedQueue<Entity>();

    public void update() {
        while (entsToAdd.peek() != null) {
            ents.add(entsToAdd.poll());
        }
        while (entsToRemove.peek() != null) {
            ents.remove(entsToRemove.poll());
        }
    }

    public Map getMap() {
        return map;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public AIManager getAiManager() {
        return aiManager;
    }

    public Iterator<Entity> getEntityIterator() {
        return ents.iterator();
    }

    /**
     * 
     * @returns a copy of the entities stored as an array
     */
    public Entity[] getEntityArray() {
        return ents.toArray(new Entity[0]);
    }

    public void addEntity(Entity ent) {
        entsToAdd.offer(ent);
    }

    public void removeEntity(Entity ent) {
        entsToRemove.offer(ent);
    }

    /**
     * Populate the world with things initially.
     * 
     * A null mapSeed will cause a map to be generated with the hard-coded test
     * seed.
     */
    public void setUpWorld(Long mapSeed) {
        if (mapSeed == null) {
            mapSeed = TEST_SEED;
        }
        map = new MapGenerator().createMap(TEST_XSIZE, TEST_YSIZE, mapSeed);
        ents = new LinkedList<>();
        aiManager = new AIManager(map);
        playerManager = new PlayerManager();
    }

    public RaycastHit raycast(Vector2D pos, Vector2D direction, int segments,
            double maxDist, Entity ignore) {
        if (segments <= 0) {
            throw new IllegalStateException("can't raycast with <= 0 segments");
        }

        Vector2D step = Vector2D.mul(direction.normalised(),
                maxDist / segments);

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
            for (Player aPlayerList : playerList) {
                if (aPlayerList == ignore) {
                    continue;
                }
                sqrSize = aPlayerList.getSize() * aPlayerList.getSize();
                sqrDist = aPlayerList.getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(RaycastHitType.PLAYER, p,
                            aPlayerList);
                }
            }
            // now against entities
            Iterator<Entity> it = this.getEntityIterator();
            while (it.hasNext()) {

                Entity ent = it.next();
                if (ent == ignore) {
                    continue;
                }
                sqrSize = ent.getSize() * ent.getSize();
                sqrDist = ent.getPosition().sqrDistTo(p);
                if (sqrDist < sqrSize) {
                    return new RaycastHit(RaycastHitType.ENTITY, p, ent);
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

    public GameManager getGameManager() {
        return gameManager;
    }

    public void changeGameManager(GameManager game) {
        gameManager = game;
    }

    public abstract void onPostEngineInit();

}
