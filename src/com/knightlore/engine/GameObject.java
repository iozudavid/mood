package com.knightlore.engine;

import com.knightlore.utils.Vector2D;

public abstract class GameObject {
    // Initialise this with a vector to avoid null pointers before
    // deserialisation occurs.
    protected Vector2D position = Vector2D.ONE;

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
        GameEngine.getSingleton().addGameObject(this);
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

    public boolean exists() {
        return exists;
    }

    // is internal to the package, should not be accessible elsewhere
    // only the game engine should call this
    void setExists(boolean b) {
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
        GameEngine.getSingleton().removeGameObject(this);
    }

}
