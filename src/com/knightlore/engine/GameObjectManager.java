package com.knightlore.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the creation, updating, deletion and general management of all
 * GameObjects. GameObjects are added to this class automatically, so the
 * process is mostly transparent.
 * 
 * @author James Adey/Joe Ellis
 *
 */
public class GameObjectManager {

    /**
     * All of the game objects currently present in existence.
     */
    private final List<GameObject> objects;

    /**
     * List of things that are due to be created in the next update. This is to
     * ensure that all game objects have their onCreate() method called before
     * their first update.
     */
    private List<GameObject> notifyToCreate;

    /**
     * List of things that are due to be destroyed in the next update. This
     * ensures that all game object have the onDestroy() method called before
     * they're destroyed.
     */
    private List<GameObject> notifyToDestroy;
    
    private boolean stillRuning=true;

    public GameObjectManager() {
        this.objects = new ArrayList<>();
        this.notifyToCreate = new LinkedList<>();
        this.notifyToDestroy = new LinkedList<>();
    }

    /**
     * Adds a game object to the GameObjectManager.
     * 
     * @param g
     *            the game object to add.
     */
    public void addGameObject(GameObject g) {
        // delay adding until next loop
        synchronized (notifyToCreate) {
            notifyToCreate.add(g);
        }
    }
    
    public void stop(){
    	//hard to stop
    	System.exit(0);
    }

    /**
     * Deletes a game object from the GameObjetManager.
     * 
     * @param g
     *            the game object to delete.
     */
    void removeGameObject(GameObject g) {
        // delay deleting until next loop
        synchronized (notifyToDestroy) {
            notifyToDestroy.add(g);
        }
    }

    /**
     * This method does 3 things: first, it traverses the notifyToCreate list
     * and creates any pending objects. It then updates existing objects.
     * Finally, it destroys any objects which are due to be removed.
     */
    protected void updateObjects() {
        // perform internal list management before updating.
        // as modifying a list whilst iterating over it is a very bad idea.
        synchronized (notifyToCreate) {
            for (GameObject obj : notifyToCreate) {
                // add the object to the update list
                objects.add(obj);
                // notify the object it has been created
                obj.onCreate();
            }
            notifyToCreate.clear();
        }
        synchronized (notifyToDestroy) {
            // remove any objects that need deleting
            for (GameObject obj : notifyToDestroy) {
                // remove the object from the update list
                objects.remove(obj);
                // notify the object it has been effectively destroyed
                obj.onDestroy();
            }
            notifyToDestroy.clear();
        }
        // update all objects
        for (GameObject obj : objects) {
        	obj.onUpdate();
        }
    }

    /**
     * Greps the list of game objects and returns a list of all of those that
     * are of a particular type.
     * 
     * @param c
     *            the class type of the objects
     * @return a list of objects of type c.
     */
    public <T> ArrayList<T> findObjectsOfType(Class<T> c) {
        ArrayList<T> results = new ArrayList<>();
        // FIXME find a nicer way of doing this
        try {
            for (GameObject object : objects) {
                if (c.isInstance(object)) {
                    results.add((T) object);
                }
            }
        } catch (ClassCastException e) {
            System.out.println("OCrap, object is instance of class, but not of same type");
            e.printStackTrace();
        }
        return results;
    }

}
