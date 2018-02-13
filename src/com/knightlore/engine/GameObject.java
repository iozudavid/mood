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

    /**
     * The position of the game object.
     */
    protected Vector2D position;

    /**
     * Whether the entity currently exists. If this variable is set to false,
     * entities will be 'garbage collected' by the game engine.
     */
    private boolean exists;

    public GameObject() {
        this(Vector2D.ZERO);
    }

    public GameObject(Vector2D position) {
        this.position = position;
        exists = true;

        getGOM().addGameObject(this);
    }

    public Vector2D getPosition() {
        return position;
    }

    public boolean exists() {
        return exists;
    }

    /**
     * Package internal; should not be accessed elsewhere. Only the game engine
     * should call this method.
     * 
     * @param b
     *            true if the game object exists, false otherwise.
     */
    protected void setExists(boolean b) {
        exists = b;
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
        getGOM().removeGameObject(this);
    }

    /**
     * Internal helper method. Gets the game engine's GOM.
     * 
     * @return the GOM.
     */
    private GameObjectManager getGOM() {
        GameEngine ge = GameEngine.getSingleton();
        GameObjectManager gom = ge.getGameObjectManager();
        return gom;
    }

}
