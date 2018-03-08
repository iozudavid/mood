package com.knightlore.render.font;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.filter.ColorFilter;

public class Font {

    public static final Font DEFAULT_BLACK = new Font("res/font/font.png", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0x000000);
    public static final Font DEFAULT_WHITE = new Font("res/font/font.png", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0xFFFFFF);

    private static final int BOUNDS_COLOR = -65536; // pure red.

    private BufferedImage sheet;
    private Map<Character, Graphic> symbols;

    public Font(String path, String order, int color) {
        this.symbols = new HashMap<Character, Graphic>();
        load(path);
        populateSymbols(order, color);
    }

    private void load(String path) {
        try {
            sheet = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateSymbols(String order, int color) {
        List<Integer> bounds = new ArrayList<Integer>();
        for (int x = 0; x < sheet.getWidth(); x++) {
            if (sheet.getRGB(x, 0) == BOUNDS_COLOR)
                bounds.add(x);
        }
        
        ColorFilter filter = new ColorFilter(color, 1D);

        for (int i = 0; i < bounds.size() - 1; i += 2) {
            int b1 = bounds.get(i), b2 = bounds.get(i + 1);
            BufferedImage character = sheet.getSubimage(b1, 1, b2 - b1 + 1, getHeight());
            Graphic g = new Graphic(character);
            filter.apply(g.getPixels(), PixelBuffer.CHROMA_KEY);
            symbols.put(order.charAt(i / 2), g);
        }
    }

    public Graphic getGraphic(char c) {
        return symbols.containsKey(c) ? symbols.get(c) : symbols.get(Character.toUpperCase(c));
    }

    public int getHeight() {
        return sheet.getHeight() - 1;
    }

}