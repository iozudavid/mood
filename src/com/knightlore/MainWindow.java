package com.knightlore;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Main window for the game.
 * 
 * @author Joe Ellis
 *
 */
public class MainWindow extends JFrame {

	public static final String TITLE = "KnightLore";

	/* Only used if !fullscreen */
	public static final int WIDTH = 1000;
	public static final int HEIGHT = WIDTH / 16 * 9; // 16:9 aspect ratio

	private boolean fullscreen;

	public MainWindow(Canvas canvas, String title) {
		this(canvas, title, -1, -1);
	}

	public MainWindow(Canvas canvas, String title, int width, int height) {
		super(TITLE);
		fullscreen = width <= 0 || height <= 0;

		if (fullscreen) {
			goFullscreen();
		} else {
			setSize(width, height);
		}

		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		setLocationRelativeTo(null);
		setCanvas(canvas);
	}

	private void setCanvas(Canvas canvas) {
		canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
		add(canvas, BorderLayout.CENTER);
		pack();
	}

	private void goFullscreen() {
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
	}

}
