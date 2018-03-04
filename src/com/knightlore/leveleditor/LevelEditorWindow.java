package com.knightlore.leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.MapSerializer;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.tile.TileType;

public class LevelEditorWindow extends JFrame {

    public static Pen pen = new Pen(TileType.brick);

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

        JMenuItem open = new JMenuItem("Open Map from File");
        open.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openMapFromFile();
            }
        });

        JMenuItem openRand = new JMenuItem("Generate Random Map");
        openRand.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openRandomMap();
            }
        });

        JMenuItem save = new JMenuItem("Save Current Map");
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapToFile();
            }

        });

        file.add(open);
        file.add(openRand);
        file.add(save);
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

        Map m = new MapGenerator().createMap(32, 32, rand.nextLong());
        panel.initialise(m);
        panel.revalidate();
        panel.repaint();
    }

    private void saveMapToFile() {
        JFileChooser fc = new JFileChooser();
        int response = fc.showSaveDialog(this);
        if (response != JFileChooser.APPROVE_OPTION)
            return;

        PrintWriter writer = null;
        try {
            File file = fc.getSelectedFile();
            writer = new PrintWriter(file);
            String serialized = MapSerializer.mapToString(panel.getMap());
            writer.print(serialized);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Something went wrong trying to save the map.", "Failed to Save Map",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            writer.close();
        }
    }

    private void openMapFromFile() {
        JFileChooser fc = new JFileChooser();
        int response = fc.showOpenDialog(this);
        if (response != JFileChooser.APPROVE_OPTION)
            return;

        Map m = null;
        try {
            String text = new String(Files.readAllBytes(Paths.get(fc.getSelectedFile().getAbsolutePath())),
                    StandardCharsets.UTF_8);
            m = MapSerializer.mapFromString(text);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Something went wrong trying to open the map.", "Failed to Open Map",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        panel.removeAll();
        panel.initialise(m);
        panel.revalidate();
        panel.repaint();
    }

}
