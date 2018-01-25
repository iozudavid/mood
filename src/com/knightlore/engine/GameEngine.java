package com.knightlore.engine;

import java.util.ArrayList;

import com.knightlore.MainWindow;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Mouse;
import com.knightlore.game.area.AreaFactory;
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

	private static final double UPDATES_PER_SECOND = 60D;
	
	private long ticker;

	private final Screen screen;
	private final MainWindow window;
	private final World world;
	private final ArrayList<GameObject> objects;
	private Thread thread;
	private volatile boolean running = false;

	public GameEngine() {
		ticker = 0;
		
		world = new World(AreaFactory.createRandomMap(Environment.LIGHT_OUTDOORS));
		objects = new ArrayList<>();

		final int w = MainWindow.WIDTH, h = MainWindow.HEIGHT;
		screen = new Screen(w, h);
		window = new MainWindow(screen, MainWindow.TITLE, w, h);
		initEngine();
	}

	private void initEngine() {
		System.out.println("Initialising Engine...");
		InputManager.init();
		setupKeyboard();
		setupMouse();
		System.out.println("Engine Initialised Successfully.");
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
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
				world.tick(ticker);
				screen.render(0, 0, world);
				delta -= 1;
			}
		}
	}

	private void updateObjects() {
		for (GameObject obj : objects) {
			obj.onUpdate(ticker);
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
