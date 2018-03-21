package com.knightlore;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import com.knightlore.render.Screen;

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

    private Screen screen;
    private boolean fullscreen;

    public MainWindow(String title) {
        this(title, -1, -1);
    }

    public MainWindow(String title, int width, int height) {
        super(TITLE);
        fullscreen = width <= 0 || height <= 0;

        setSize(width, height);
        setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setLocationRelativeTo(null);
    }

    private void setCanvas(Canvas canvas) {
        canvas.setPreferredSize(new Dimension(getWidth(), getHeight()));
        add(canvas, BorderLayout.CENTER);
        pack();
    }

    public void finalise() {
        int w = WIDTH, h = HEIGHT;
        if (fullscreen) {
            goFullscreen();
            Dimension resolution = Screen.getScreenResolution();
            w = resolution.width;
            h = resolution.height;
        }

        this.screen = new Screen(w, h);
        setCanvas(this.screen);
    }

    public Screen getScreen() {
        return screen;
    }

    public void goFullscreen() {
        GraphicsEnvironment gd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = gd.getDefaultScreenDevice();
        device.setFullScreenWindow(this);
    }
    
}
