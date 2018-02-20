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

    public GameObjectManager() {
        this.objects = new ArrayList<GameObject>();
        this.notifyToCreate = new LinkedList<GameObject>();
        this.notifyToDestroy = new LinkedList<GameObject>();
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
            Iterator<GameObject> it = notifyToCreate.iterator();
            while (it.hasNext()) {
                GameObject obj = it.next();
                // add the object to the update list
                objects.add(obj);
                obj.setExists(true);
                // notify the object it has been created
                obj.onCreate();
            }
            notifyToCreate.clear();
        }
        synchronized (notifyToDestroy) {
            // remove any objects that need deleting
            Iterator<GameObject> it = notifyToDestroy.iterator();
            while (it.hasNext()) {
                GameObject obj = it.next();
                // remove the object from the update list
                objects.remove(obj);
                obj.setExists(false);
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

}
