package com.knightlore.engine;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.knightlore.GameSettings;
import com.knightlore.MainWindow;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Mouse;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.game.world.GameWorld;
import com.knightlore.game.world.ServerWorld;
import com.knightlore.gui.GUIState;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.client.ClientManager;
import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.server.ServerManager;
import com.knightlore.network.server.ServerNetworkObjectManager;
import com.knightlore.render.Camera;
import com.knightlore.render.Display;
import com.knightlore.render.GameFeed;
import com.knightlore.render.Renderer;
import com.knightlore.render.Screen;
import com.knightlore.render.hud.HUD;
import com.knightlore.render.minimap.Minimap;

/**
 * Game engine acting as sort of a 'hub' for each of the individual game
 * components.
 *
 * @authors Joe Ellis, James Adey
 */
public class GameEngine implements Runnable {
    
    public static final double UPDATES_PER_SECOND = 60D;
    public static final Ticker ticker = new Ticker();
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static boolean HEADLESS;
    private static GameEngine singleton = null;
    public GUIState guiState = GUIState.START_MENU;
    private Thread thread;
    private volatile boolean running = false;
    private MainWindow window;
    private Screen screen;
    private Display display;
    private GameWorld world;
    private GameObjectManager gameObjectManager;
    private NetworkObjectManager networkObjectManager;
    private Camera camera;
    private float defaultVolume = -1;
    private SoundManager soundManager;
    private boolean _doneInit = false;
    
    private GameEngine() {
        if (HEADLESS) {
            window = null;
        } else {
            createWindow();
        }
        
        this.gameObjectManager = new GameObjectManager();
    }
    
    /**
     * @returns the GameEngine singleton, or creates one if none exists
     */
    public static GameEngine getSingleton() {
        if (singleton == null) {
            singleton = new GameEngine();
        }
        return singleton;
    }
    
    public boolean doneInit() {
        return _doneInit;
    }
    
    public SoundManager getSoundManager() {
        return soundManager;
    }
    
    public GameObjectManager getGameObjectManager() {
        return gameObjectManager;
    }
    
    public NetworkObjectManager getNetworkObjectManager() {
        return networkObjectManager;
    }
    
    /**
     * Initialises the core engine components. Initialises the Input Manager,
     * sets up keyboard and mouse, and creates the display.
     */
    public void initEngine() {
        System.out.println("Initialising Engine...");
        if (!HEADLESS) {
            InputManager.init();
            setupKeyboard();
            setupMouse();
        }
        
        if (GameSettings.isClient()) {
            this.display = new Display();
        }
        System.out.println("Engine Initialised Successfully.");
    }
    
    /**
     * Begins the game. Creates the world, and starts a new thread for the
     * networking. Then populates the world and the display with the HUD
     * components.
     */
    public void startGame() {
        System.out.println("Starting game...");
        // The NetworkObjectManager will call setUpWorld() on the world when
        // it's ready to do so.
        if (defaultVolume != -1) {
            this.soundManager = new SoundManager(defaultVolume);
        } else {
            this.soundManager = new SoundManager();
        }
        
        if (GameSettings.isServer()) {
            world = new ServerWorld();
            networkObjectManager = new ServerNetworkObjectManager((ServerWorld)world);
            ServerManager networkManager = new ServerManager();
            new Thread(networkManager).start();
            ((ServerNetworkObjectManager)networkObjectManager).init();
        }
        
        if (GameSettings.isClient()) {
            world = new ClientWorld();
            networkObjectManager = new ClientNetworkObjectManager((ClientWorld)world);
            ClientManager networkManager;
            try {
                networkManager = new ClientManager();
            } catch (Exception e) {
                throw new RuntimeException();
            }
            new Thread(networkManager).start();
            ((ClientNetworkObjectManager)networkObjectManager).init(networkManager.getServerSender());
        }
        
        System.out.println("Initialising NetworkObjectManager...");
        
        System.out.println("World Initialised Successfully.");
        
        if (GameSettings.isClient()) {
            ClientNetworkObjectManager cn = (ClientNetworkObjectManager)networkObjectManager;
            while (!cn.hasFinishedSetup()) {
                // wait...
            }
            final int w = screen.getWidth(), h = screen.getHeight();
            ClientWorld cworld = (ClientWorld)world;
            cworld.setScreenHeight(h);
            cworld.setScreenWidth(w);
            Renderer renderer = new Renderer(w, 8 * h / 9, camera, cworld);
            Minimap minimap = new Minimap(camera, cworld, 128);
            HUD hud = new HUD(cn.getMyPlayer(), w, h / 9);
            this.display.setHud(hud);
            this.display.setMinimap(minimap);
            this.display.setRenderer(renderer);
            
        }
        this.guiState = GUIState.IN_GAME;
        
        // start the lobby
        world.getGameManager().startLobby();
        world.getGameManager().beginGame();
        // build anything that requires the renderer
        // think gui
        System.out.println("Engine init complete.");
        world.onPostEngineInit();
        _doneInit = true;
        
    }
    
    /**
     * Creates the window for the game.
     */
    private void createWindow() {
        if (GameSettings.FULLSCREEN) {
            window = new MainWindow();
        } else {
            final int w = MainWindow.WIDTH, h = MainWindow.HEIGHT;
            window = new MainWindow(w, h);
        }
        window.finalise();
        this.screen = window.getScreen();
    }
    
    /**
     * Starts a new thread to handle the game.
     */
    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
        if (!HEADLESS) {
            window.setVisible(true);
        }
    }
    
    /**
     * Used to stop the game, resets the GameObjectManager so a new game can be
     * started. e.g. when returning to the main menu.
     */
    public void stopGame() {
        synchronized (this.gameObjectManager) {
            this.gameObjectManager = new GameObjectManager();
        }
    }
    
    /**
     * Stops the game, and exits the program.
     */
    public void stop() {
        this.gameObjectManager.stop();
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // should't happen as the thread shouldn't be interrupted
            e.printStackTrace();
        }
        if (GameSettings.isClient()) {
            window.dispose();
        }
    }
    
    /**
     * Contains the main game loop.
     */
    @Override
    public void run() {
        // start the lobby
        // finally... begin!
        
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
                synchronized (this.gameObjectManager) {
                    gameObjectManager.updateObjects();
                }
                if (guiState == GUIState.IN_GAME && world != null) {
                    world.update();
                    GameFeed.getInstance().update();
                }
                delta -= 1;
                ticker.tick();
            }
            if (!HEADLESS) {
                screen.render(0, 0, display);
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
    
    public Display getDisplay() {
        return this.display;
    }
    
    public void setVolume(float newVolume) {
        this.defaultVolume = newVolume;
    }
}
