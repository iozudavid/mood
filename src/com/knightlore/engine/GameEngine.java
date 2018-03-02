package com.knightlore.engine;

import com.knightlore.GameSettings;
import com.knightlore.MainWindow;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Mouse;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.game.world.GameWorld;
import com.knightlore.game.world.ServerWorld;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.client.ClientManager;
import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.server.ServerManager;
import com.knightlore.network.server.ServerNetworkObjectManager;
import com.knightlore.render.Camera;
import com.knightlore.render.Renderer;
import com.knightlore.render.Screen;

/**
 * Game engine acting as sort of a 'hub' for each of the individual game
 * components.
 * 
 * @authors Joe Ellis, James Adey
 *
 */
public class GameEngine implements Runnable {

    public static boolean HEADLESS;

    private static GameEngine singleton = null;

    private Thread thread;
    private volatile boolean running = false;

    public static final double UPDATES_PER_SECOND = 60D;
    public static final Ticker ticker = new Ticker();

    private MainWindow window;
    private Screen screen;

    private GameWorld world;
    private GameObjectManager gameObjectManager;
    private NetworkObjectManager networkObjectManager;
    private Camera camera;
    private Renderer renderer;

    private GameEngine() {
        if (HEADLESS) {
            window = null;
        } else {
            createWindow();
        }

        this.gameObjectManager = new GameObjectManager();
    }

    public static GameEngine getSingleton() {
        if (singleton == null) {
            singleton = new GameEngine();
        }
        return singleton;
    }

    public GameObjectManager getGameObjectManager() {
        return gameObjectManager;
    }

    // FIXME: remove this
    public Renderer getRenderer() {
        return this.renderer;
    }

    public NetworkObjectManager getNetworkObjectManager() {
        return networkObjectManager;
    }

    /**
     * Initialises the game engine.
     */
    public void initEngine() {
        System.out.println("Initialising Engine...");
        if (!HEADLESS) {
            InputManager.init();
            setupKeyboard();
            setupMouse();
        }

        System.out.println("Engine Initialised Successfully.");
        // TODO maybe refactor this into a make world method
        // ALSO TODO, UNHOOK TEST WORLD
        System.out.println("Initialising World...");

        // The NetworkObjectManager will call setUpWorld() on the world when
        // it's ready to do so.
        if (GameSettings.isServer()) {
            ServerManager networkManager = new ServerManager();
            new Thread(networkManager).start();
            world = new ServerWorld();
            networkObjectManager = new ServerNetworkObjectManager(
                    (ServerWorld) world);
        }
        if (GameSettings.isClient()) {
            ClientManager networkManager = new ClientManager();
            new Thread(networkManager).start();
            world = new ClientWorld();
            networkObjectManager = new ClientNetworkObjectManager(
                    (ClientWorld) world);
        }

        System.out.println("Initialising NetworkObjectManager...");
        networkObjectManager.init();

        System.out.println("World Initialised Successfully.");

        if (GameSettings.isClient()) {
            this.renderer = new Renderer(camera, (ClientWorld) world);
        }
    }

    /**
     * Creates the window for the game.
     */
    private void createWindow() {
        if (GameSettings.FULLSCREEN) {
            final int w = MainWindow.WIDTH, h = MainWindow.HEIGHT;
            window = new MainWindow(MainWindow.TITLE, w, h);
        } else {
            window = new MainWindow(MainWindow.TITLE);
        }
        window.finalise();
        this.screen = window.getScreen();
    }

    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
        if (!HEADLESS) {
            window.setVisible(true);
        }
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
                gameObjectManager.updateObjects();
                world.update();
                delta -= 1;
                ticker.tick();
            }

            if (!HEADLESS) {
                screen.render(0, 0, renderer);
                InputManager.clearMouse();
            }
        }
        System.err.println("GameEngine finished running.");
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

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public GameWorld getWorld() {
        return world;
    }

}
