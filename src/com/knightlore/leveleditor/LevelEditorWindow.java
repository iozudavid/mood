package com.knightlore.leveleditor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import com.knightlore.game.GameType;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.tile.BrickTile;

public class LevelEditorWindow extends JFrame {

    public static final Pen pen = new Pen(new BrickTile());

    public static final String TITLE = "KnightLore Level Editor";
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;

    private final LevelEditorPanel panel;

    public LevelEditorWindow(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem open = new JMenuItem("Open Map from File");
        open.addActionListener(e -> openMapFromFile());

        JMenuItem openRand = new JMenuItem("Generate Random Map");
        openRand.addActionListener(e -> openRandomMap());

        JMenuItem save = new JMenuItem("Save Current Map");
        save.addActionListener(e -> saveMapToFile());

        file.add(open);
        file.add(openRand);
        file.add(save);
        menuBar.add(file);
        setJMenuBar(menuBar);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        panel = new LevelEditorPanel(new MapGenerator().createMap(32, 32, GameType.TDM, 20L));
        pane.setLeftComponent(new TileChooserPanel());
        pane.setRightComponent(panel);

        add(pane);
        setVisible(true);
    }

    private void openRandomMap() {
        Random rand = new Random();
        panel.removeAll();

        Map m = new MapGenerator().createMap(32, 32, GameType.TDM, rand.nextLong());
        panel.initialise(m);
        panel.revalidate();
        panel.repaint();
    }

    private void saveMapToFile() {
        JFileChooser fc = new JFileChooser();
        int response = fc.showSaveDialog(this);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile());
                ObjectOutputStream objectOut = new ObjectOutputStream(fos)) {
            objectOut.writeObject(panel.getMap());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Something went wrong trying to save the map.", "Failed to Save Map",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMapFromFile() {
        JFileChooser fc = new JFileChooser();
        int response = fc.showOpenDialog(this);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(fc.getSelectedFile());
                ObjectInputStream objectIn = new ObjectInputStream(fis)) {
            Map m = (Map) objectIn.readObject();
            panel.removeAll();
            panel.initialise(m);
            panel.revalidate();
            panel.repaint();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Something went wrong trying to open the map.", "Failed to Open Map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
