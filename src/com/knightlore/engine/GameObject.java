package com.knightlore.engine;

import com.knightlore.utils.Vector2D;

public abstract class GameObject {
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

    // is internal to the package, should not be accessible elsewhere
    // only the game engine should call this
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

    public void destroy() {
        getGOM().removeGameObject(this);
    }

    private GameObjectManager getGOM() {
        GameEngine ge = GameEngine.getSingleton();
        GameObjectManager gom = ge.getGameObjectManager();
        return gom;
    }

}
