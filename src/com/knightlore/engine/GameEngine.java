package com.knightlore.engine;

import com.knightlore.MainWindow;
import com.knightlore.input.Mouse;
import com.knightlore.render.Screen;
import com.knightlore.render.environment.LightOutdoorsEnvironment;

/**
 * Game engine acting as sort of a 'hub' for each of the individual game
 * components.
 * 
 * @author Joe Ellis
 *
 */
public class GameEngine implements Runnable {

	private volatile boolean running = false;
	private Thread thread;
	private final double updatesPerSecond = 60D;

	private Screen screen;
	private MainWindow window;

	private World world;

	public GameEngine() {
		world = new World(new LightOutdoorsEnvironment());

		final int w = MainWindow.WIDTH, h = MainWindow.HEIGHT;
		screen = new Screen(w, h);
		window = new MainWindow(screen, MainWindow.TITLE, w, h);
		InitEngine();
	}

	private void InitEngine() {
		Input.init();
		setupKeyboard();
		setupMouse();
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
		window.setVisible(true);
	}

	private void stop() {
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
		 * whatever it is set to in the variable uupdatesPerSecond.
		 */

		long lastTime = System.nanoTime();
		double delta = 0D;
		double ns = 1E9D / updatesPerSecond;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			while (delta >= 1) {
				world.tick();
				screen.render(0, 0, world);
				delta -= 1;
			}

		}
	}

	/**
	 * Add the singleton keyboard instance to the canvas and request focus.
	 */
	private void setupKeyboard() {
		screen.addKeyListener(Input.GetKeyboard());
		screen.requestFocus();
	}

	/**
	 * Add the singleton mouse instance to the canvas and request focus.
	 */
	private void setupMouse() {
		Mouse mouse = Input.GetMouse();
		screen.addMouseListener(mouse);
		screen.addMouseMotionListener(mouse);
		screen.addMouseWheelListener(mouse);
		screen.requestFocus();
	}

	public static void main(String[] args) throws InterruptedException {
		GameEngine engine = new GameEngine();
		engine.start();
	}

}
