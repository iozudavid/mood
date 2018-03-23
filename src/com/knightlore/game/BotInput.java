package com.knightlore.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Player;
import com.knightlore.game.manager.PlayerManager;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.manager.AIManager;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.utils.physics.RaycastHit;
import com.knightlore.utils.Vector2D;

/**
 * Input module for those players that are controlled by bots
 * 
 * @authors James, Tom
 *
 */
public final class BotInput extends InputModule {
    
    private static final long THINK_DELAY = 6;
    private static final double SIGHT_DIST = 7;
    private static final double SQR_SIGHT_DIST = SIGHT_DIST * SIGHT_DIST;
    private static final double ACC = 0.1;
    private static final double SQR_DIST_TO_NODE = 0.6;
    private static final double MOVE_ACC = 0.2;
    private static final byte ONE = 1;
    private static final byte ZERO = 0;
    private long nextThinkTime = 0;
    private long fov = 60;
    private Entity target = null;
    private Vector2D goalPos = Vector2D.ZERO;
    private List<Point> path = new ArrayList<>();
    private Vector2D lookPos = Vector2D.ZERO;
    
    @Override
    public Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState, Entity myPlayer) {
        long currentTime = GameEngine.ticker.getTime();
        if (nextThinkTime < currentTime) {
            think(myPlayer);
            nextThinkTime = currentTime + THINK_DELAY;
        }
        
        // shoot and aim
        aim(myPlayer);
        
        // follow the path
        move(myPlayer);
        
        return axesToInput(inputState);
    }
    
    private void move(Entity myPlayer) {
        Vector2D displacement = goalPos.subtract(myPlayer.getPosition());
        double dotRight = displacement.dot(myPlayer.getDirection().perpendicular());
        double dotForward = displacement.dot(myPlayer.getDirection());
        double sqrDist = displacement.sqrMagnitude();
        
        strafeInput = dblToAxis(dotRight, MOVE_ACC);
        walkInput = dblToAxis(dotForward, MOVE_ACC);
        //System.out.println(myPlayer.getName() + "->" + sqrDist);
        if (path.isEmpty()) {
            getPath(myPlayer);
            strafeInput = 0;
            walkInput = 0;
        } else if (sqrDist < SQR_DIST_TO_NODE) {
            // remove last node
            path.remove(0);
            if (!path.isEmpty()) {
                // move to next node
                goalPos = Vector2D.fromTilePoint(path.get(0));
            }
        }
        
    }
    
    private void aim(Entity myPlayer) {
        
        Vector2D displacement = lookPos.subtract(myPlayer.getPosition());
        double dotRight = displacement.dot(myPlayer.getDirection().perpendicular());
        turnInput = dblToAxis(dotRight, ACC);
        if (target != null) {
            lookPos = target.getPosition();
            shoot = true;
        } else {
            lookPos = goalPos;
            shoot = false;
        }
    }
    
    private double dblToAxis(double val, double acc) {
        if (val > acc) {
            return 1;
        } else if (val < -acc) {
            return -1;
        } else {
            return 0;
        }
        
    }
    
    private void getPath(Entity myPlayer) {
        Vector2D goal = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        AIManager aiManager = GameEngine.getSingleton().getWorld().getAiManager();
        path = aiManager.findRawPath(myPlayer.getPosition(), goal);
        goalPos = myPlayer.getPosition();
    }
    
    private void think(Entity myPlayer) {
        target = null;
        // Find our target
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        Iterator<Player> playerIter = playerManager.getPlayerIterator();
        while (playerIter.hasNext()) {
            Player player = playerIter.next();
            Vector2D displacement = player.getPosition().subtract(myPlayer.getPosition());
            Vector2D dir = displacement.normalised();

            double sqrDist = displacement.sqrMagnitude();
            // check if out of our sight distance
            if (sqrDist > SQR_SIGHT_DIST) {
                continue;
            }

            double dot = dir.dot(myPlayer.getDirection().normalised());
            double cosFOV = Math.cos(Math.toRadians(fov));
            // check out of field of view
            if (dot > cosFOV) {
                continue;
            }

            // check actual Line of sight
            RaycastHit hit = GameEngine.getSingleton().getWorld().raycast(myPlayer.getPosition(), dir, 100, SIGHT_DIST,
                    myPlayer);
            // did we not hit an ENTITY?
            if (!hit.didHitEntity()) {
                continue;
            }
            // check target
            if (hit.getEntity() != player) {
                continue;
            }
            target = player;
            return;
        }
    }
    
    private Map<ClientController, Byte> axesToInput(Map<ClientController, Byte> inputState) {
        if (walkInput > 0) {
            inputState.put(ClientController.FORWARD, ONE);
            inputState.put(ClientController.BACKWARD, ZERO);
        } else if (walkInput < 0) {
            inputState.put(ClientController.FORWARD, ZERO);
            inputState.put(ClientController.BACKWARD, ONE);
        } else {
            inputState.put(ClientController.FORWARD, ZERO);
            inputState.put(ClientController.BACKWARD, ZERO);
        }
        
        if (strafeInput > 0) {
            inputState.put(ClientController.RIGHT, ONE);
            inputState.put(ClientController.LEFT, ZERO);
            
        } else if (strafeInput < 0) {
            inputState.put(ClientController.RIGHT, ZERO);
            inputState.put(ClientController.LEFT, ONE);
        } else {
            inputState.put(ClientController.RIGHT, ZERO);
            inputState.put(ClientController.LEFT, ZERO);
        }
        
        if (turnInput > 0) {
            inputState.put(ClientController.ROTATE_CLOCKWISE, ONE);
            inputState.put(ClientController.ROTATE_ANTI_CLOCKWISE, ZERO);
            
        } else if (turnInput < 0) {
            inputState.put(ClientController.ROTATE_CLOCKWISE, ZERO);
            inputState.put(ClientController.ROTATE_ANTI_CLOCKWISE, ONE);
        } else {
            inputState.put(ClientController.ROTATE_CLOCKWISE, ZERO);
            inputState.put(ClientController.ROTATE_ANTI_CLOCKWISE, ZERO);
        }
        
        if (shoot) {
            inputState.put(ClientController.SHOOT, ONE);
        } else {
            inputState.put(ClientController.SHOOT, ZERO);
        }
        
        return inputState;
    }
    
    @Override
    public void onRespawn(Entity myPlayer) {
        getPath(myPlayer);
        
    }
    
}
