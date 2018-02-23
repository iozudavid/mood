package com.knightlore.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.knightlore.GameSettings;
import com.knightlore.MainWindow;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Mouse;
import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.server.ServerNetworkObjectManager;
import com.knightlore.render.Camera;
import com.knightlore.render.Renderer;
import com.knightlore.render.Screen;
import com.knightlore.world.TestWorld;

/**
 * Game engine acting as sort of a 'hub' for each of the individual game
 * components.
 * 
 * @authors Joe Ellis, James Adey
 *
 */
public class GameEngine implements Runnable {

    private static GameEngine singleton = null;

    private static final double UPDATES_PER_SECOND = 60D;
    public static final Ticker ticker = new Ticker();
    private Screen screen;
    private final MainWindow window;
    private GameWorld world;

    private Renderer renderer;
    private final ArrayList<GameObject> objects;
    private Thread thread;
    private volatile boolean running = false;

    private LinkedList<GameObject> notifyToCreate;
    private LinkedList<GameObject> notifyToDestroy;

    public final boolean headless;

    public GameEngine(boolean headless) {
        this.headless = headless;

        singleton = this;

        objects = new ArrayList<GameObject>();

        notifyToCreate = new LinkedList<GameObject>();
        notifyToDestroy = new LinkedList<GameObject>();


        world = new TestWorld();
        
        if (GameSettings.isClient())
            new ClientNetworkObjectManager().init();
        if (GameSettings.isServer())
            new ServerNetworkObjectManager().init();

        if (headless) {
            window = null;
        } else {
            final int w = MainWindow.WIDTH, h = MainWindow.HEIGHT;
            window = new MainWindow(MainWindow.TITLE, w, h);
            window.finalise();
            this.screen = window.getScreen();
        }
        initEngine();
    }

    public static GameEngine getSingleton() {
        return singleton;
    }

    void addGameObject(GameObject g) {
        System.out.println("adding object " + g.getClass().getName());
        // delay adding until next loop
        synchronized (notifyToCreate) {
            notifyToCreate.add(g);
        }
    }

    void removeGameObject(GameObject g) {
        // delay deleting until next loop
        synchronized (notifyToDestroy) {
            notifyToDestroy.add(g);
        }
    }

    // FIXME: remove this
    public Renderer getRenderer() {
        return this.renderer;
    }

    private void initEngine() {
        System.out.println("Initialising Engine...");
        if (!headless) {
            InputManager.init();
            setupKeyboard();
            setupMouse();
        }

        System.out.println("Engine Initialised Successfully.");
        // TODO maybe refactor this into a make world method
        // ALSO TODO, UNHOOK TEST WORLD
        System.out.println("Initialising World...");
        System.out.println("initialising...");
        world.initWorld();
        System.out.println("populating...");
        world.populateWorld();
        System.out.println("World Initialised Successfully.");
        renderer = new Renderer(Camera.mainCamera(), world);
    }

    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
        if (!headless)
            window.setVisible(true);
    }

    public void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // should't happen as the thread shouldn't be interrupted
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /*
         * This piece of code limits the number of game updates per second to
         * whatever it is set to in the variable updatesPerSecond.
         */
        long lastTime = System.nanoTime();
        double delta = 0D;
        double ns = 1E9D / UPDATES_PER_SECOND;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                updateObjects();
                delta -= 1;
                ticker.tick();
            }

            if (!headless) {
                screen.render(0, 0, renderer);
                InputManager.clearMouse();
            }
        }
        System.err.println("GameEngine finished running.");
    }

    private void updateObjects() {

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

    /**
     * Add the singleton keyboard instance to the canvas and request focus.
     */
    private void setupKeyboard() {
        screen.addKeyListener(InputManager.getKeyboard());
        screen.requestFocus();
    }

    /**
     * Add the singleton mouse instance to the canvas and request focus.
     */
    private void setupMouse() {
        Mouse mouse = InputManager.getMouse();
        screen.addMouseListener(mouse);
        screen.addMouseMotionListener(mouse);
        screen.addMouseWheelListener(mouse);
        screen.requestFocus();
    }

}
