package com.knightlore.engine;

import com.knightlore.utils.Vector2D;

/**
 * A game object is any structure/entity in the game that has a 2D position.
 * They are coupled with the GameObjectManager class, which handles creating
 * updating, deleting and managing instances of this class.
 * 
 * @author James Adey
 *
 */
public abstract class GameObject {

    protected Vector2D position;

    /**
     * Whether the entity currently exists. If this variable is set to false,
     * entities will be 'garbage collected' by the game engine.
     */
    protected boolean exists = false;

    public GameObject() {
        this(Vector2D.ZERO);
    }

    public GameObject(Vector2D position) {
        this.position = position;
    }

    /**
     * Adds this object to the GameObjectMaanager
     */
    public void init() {
        exists = true;
        getGOM().addGameObject(this);
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getxPos() {
        return getPosition().getX();
    }

    public double getyPos() {
        return getPosition().getY();
    }

    public void setxPos(double xPos) {
        position = new Vector2D(xPos, position.getY());
    }

    public void setyPos(double yPos) {
        position = new Vector2D(position.getX(), yPos);
    }
    
    /**
     * Called when the component is first added to the GameObject, before it is
     * first updated. Use this to subscribe to listeners.
     */
    public abstract void onCreate();

    /**
     * Called every game frame.
     */
    public abstract void onUpdate();

    /**
     * Called when the attached GameObject is being removed from the game, it
     * will no longer receive updates. Use this to unsubscribe from event
     * listeners.
     */
    public abstract void onDestroy();

    /**
     * Removes this game object from the game object manager.
     */
    public void destroy() {
        exists = false;
        getGOM().removeGameObject(this);
    }

    /**
     * Internal helper method. Gets the game engine's GOM.
     * 
     * @return the GOM.
     */
    private GameObjectManager getGOM() {
        GameEngine ge = GameEngine.getSingleton();
        return ge.getGameObjectManager();
    }

}
