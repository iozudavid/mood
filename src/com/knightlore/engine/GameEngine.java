package com.knightlore.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.knightlore.MainWindow;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Mouse;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.render.Environment;
import com.knightlore.render.Screen;

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

	private Renderer renderer;
	private final ArrayList<GameObject> objects;
	private Thread thread;
	private volatile boolean running = false;

	private LinkedList<GameObject> notifyToCreate;
	private LinkedList<GameObject> notifyToDestroy;

	private boolean headless;

	public GameEngine(boolean headless) {
		this.headless = headless;
		singleton = this;

		objects = new ArrayList<GameObject>();

		notifyToCreate = new LinkedList<GameObject>();
		notifyToDestroy = new LinkedList<GameObject>();

		if (headless)
			window = null;
		else {
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
		MapGenerator generator  = new MapGenerator();
		Map map = generator.createMap(64, 64, Environment.LIGHT_OUTDOORS);
		//Camera camera = new Camera(4.5, 4.5, 1, 0, 0, Camera.FIELD_OF_VIEW,
		//		map);
		renderer = new Renderer(null, map);
		System.out.println("Engine Initialised Successfully.");
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
            if (!headless)
                screen.render(0, 0, renderer);
		}
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
