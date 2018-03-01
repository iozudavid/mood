package com.knightlore.leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import com.knightlore.game.area.generation.MapGenerator;

public class LevelEditorWindow extends JFrame {

    public static Pen pen = new Pen(Pen.ETile.BRICK);

    public static final String TITLE = "KnightLore Level Editor";
    public static final int WIDTH = 1000;
    public static final int HEIGHT = WIDTH;

    private LevelEditorPanel panel;

    public LevelEditorWindow(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem open = new JMenuItem("Generate Random Map");
        open.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openRandomMap();
            }
        });

        file.add(open);
        menuBar.add(file);
        setJMenuBar(menuBar);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        panel = new LevelEditorPanel(new MapGenerator().createMap(32, 32, 20L));
        pane.setLeftComponent(new TileChooserPanel());
        pane.setRightComponent(panel);

        add(pane);
        setVisible(true);
    }

    private void openRandomMap() {
        Random rand = new Random();
        panel.removeAll();
        panel.initialise(new MapGenerator().createMap(32, 32, rand.nextLong()));
        panel.revalidate();
        panel.repaint();
    }

}
