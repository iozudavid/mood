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
 * Input module for those players that are controlled by bots.
 * 
 * @authors James Adey, Tom Wiliams
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
    private static final long FOV = 60;
    private long nextThinkTime = 0;
    private Entity target = null;
    private Vector2D goalPos = Vector2D.ZERO;
    private List<Point> path = new ArrayList<>();
    private Vector2D lookPos = Vector2D.ZERO;
    
    /**
     * Runs the main AI code, generates inputs for each AI player.
     * 
     * @returns the new InputModule for the supplied player
     */
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
    
    /**
     * Updates the move inputs for this Bot. Sets the walk and strafe inputs. in
     * the input map.
     * 
     * @param myPlayer
     *            the Player that this input module is serving
     */
    private void move(Entity myPlayer) {
        Vector2D displacement = goalPos.subtract(myPlayer.getPosition());
        double dotRight = displacement.dot(myPlayer.getDirection().perpendicular());
        double dotForward = displacement.dot(myPlayer.getDirection());
        double sqrDist = displacement.sqrMagnitude();
        
        strafeInput = dblToAxis(dotRight, MOVE_ACC);
        walkInput = dblToAxis(dotForward, MOVE_ACC);
        // System.out.println(myPlayer.getName() + "->" + sqrDist);
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
    
    /**
     * Determines where to look, which way to turn and when to shoot.
     * 
     * @param myPlayer
     *            the Player that this input module is serving
     */
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
    
    /**
     * A helper method to convert a double value to an input axis. Input axes
     * are floating point values always between -1 and 1. This method takes a
     * double, and a threshold and returns which way <code>val</code> must
     * change if it is to fit within the threshold.
     * 
     * @param val
     *            the value to threshold
     * @param acc
     *            A value used to form the threshold spanning <code>-acc</code>
     *            to 0 to <code>acc</code>.
     * @returns
     *          <li>1 if <code>val</code> > <code>acc</code>
     *          <li>-1 if <code>val</code> < <code>acc</code>
     *          <li>0 if <code>acc</code> <= <code>val</code> <=
     *          <code>acc</code>
     */
    private static double dblToAxis(double val, double acc) {
        if (val > acc) {
            return 1;
        } else if (val < -acc) {
            return -1;
        } else {
            return 0;
        }
        
    }
    
    /**
     * A helper method that populates the path stored for this bot. Currently,
     * this is the path to random spawn postitions.
     * 
     * @param myPlayer
     *            the Player that this input module is serving
     */
    private void getPath(Entity myPlayer) {
        Vector2D goal = GameEngine.getSingleton().getWorld().getMap().getRandomSpawnPoint();
        AIManager aiManager = GameEngine.getSingleton().getWorld().getAiManager();
        path = aiManager.findRawPath(myPlayer.getPosition(), goal);
        goalPos = myPlayer.getPosition();
    }
    
    /**
     * Decides on the target that this bot should be aiming atF
     * 
     * @param myPlayer
     *            the Player that this input module is serving
     */
    private void think(Entity myPlayer) {
        target = null;
        // Find our target
        PlayerManager playerManager = GameEngine.getSingleton().getWorld().getPlayerManager();
        Iterator<Player> playerIter = playerManager.getPlayerIterator();
        while (playerIter.hasNext()) {
            Player player = playerIter.next();
            
            // check for friendlies
            if(myPlayer.team != Team.NONE && player.team == myPlayer.team) {
                continue;
            }
            
            
            Vector2D displacement = player.getPosition().subtract(myPlayer.getPosition());
            Vector2D dir = displacement.normalised();
            
            double sqrDist = displacement.sqrMagnitude();
            // check if out of our sight distance
            if (sqrDist > SQR_SIGHT_DIST) {
                continue;
            }
            
            double dot = dir.dot(myPlayer.getDirection().normalised());
            double cosFOV = Math.cos(Math.toRadians(FOV));
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
    
    /**
     * Fills the map provided with mappings representing the input axes of this
     * bot.
     * 
     * @param inputState
     *            the inputState to fill wiht mappings
     * @returns a modified <code>inputState</code> containing new mappings
     */
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
    
    /**
     * Gets the path for the bot when the player respawns.
     */
    @Override
    public void onRespawn(Entity myPlayer) {
        getPath(myPlayer);
        
    }
    
}
