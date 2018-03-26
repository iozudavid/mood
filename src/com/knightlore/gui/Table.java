package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;

/**
 * Class used to create a table(i.e scoreboard)
 *
 * @author David Iozu
 */
public class Table extends GUIObject {
    
    private final HashMap<String, Double> headersAndWidth;
    //...
    private final CopyOnWriteArrayList<CopyOnWriteArrayList<String>> entries;
    
    public Table(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
        this.headersAndWidth = new LinkedHashMap<>();
        this.entries = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Set the table header.
     *
     * @param headers - list of headers to be set.
     */
    public void setTableHeader(ArrayList<String> headers) {
        int maxNoOfChars = 0;
        for (String t : headers) {
            maxNoOfChars += t.toCharArray().length;
        }
        for (String t : headers) {
            this.headersAndWidth.put(t, (double)t.toCharArray().length / (double)maxNoOfChars);
        }
    }
    
    /**
     * Add an entry to the table.
     *
     * @param entry - list of entries to be added.
     */
    public void addTableEntry(CopyOnWriteArrayList<String> entry) {
        synchronized (this.entries) {
            for (CopyOnWriteArrayList<String> elem : this.entries) {
                if (elem.get(0).equals(entry.get(0))) {
                    this.entries.remove(elem);
                    this.entries.add(entry);
                    return;
                }
            }
            this.entries.add(entry);
        }
    }
    
    /**
     * Remove an entry from the table.
     *
     * @param uuid - UUID entry to be removed
     */
    public void removeTableEntry(String uuid) {
        synchronized (this.entries) {
            CopyOnWriteArrayList<String> toBeRemoved = null;
            for (CopyOnWriteArrayList<String> entry : this.entries) {
                if (entry.get(0).equals(uuid)) {
                    toBeRemoved = entry;
                    break;
                }
            }
            if (toBeRemoved != null) {
                this.entries.remove(toBeRemoved);
            }
        }
    }
    
    /**
     * Order it in descending order.
     */
    private void orderTableByScore() {
        if (this.entries.size() < 2) {
            return;
        }
        try {
            this.entries.sort((o1, o2) -> {
                int score1 = Integer.parseInt(o1.get(o1.size() - 1));
                int score2 = Integer.parseInt(o2.get(o2.size() - 1));
                if (score1 == score2) {
                    return 0;
                }
                
                return score1 > score2 ? -1 : 1;
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Draw the table in descending order.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        synchronized (this.entries) {
            int color = Color.DARK_GRAY.getRGB();
            int totalHeight;
            totalHeight = (Font.DEFAULT_WHITE.getHeight() + 15) * (1 + this.entries.size());
            pix.fillRect(color, rect.x, rect.y, rect.width, totalHeight);
            color = Color.WHITE.getRGB();
            int lastX = rect.x;
            int lastY = rect.y;
            
            ArrayList<Double> entriesWidth = new ArrayList<>();
            
            // render the header
            for (String t : this.headersAndWidth.keySet()) {
                // draw cell
                pix.drawRect(color, lastX, lastY, (int)(this.rect.width * this.headersAndWidth.get(t)),
                        totalHeight);
                // draw text
                int hOffset = Font.DEFAULT_WHITE.getHeight();
                // draw the characters of the string
                pix.drawString(Font.DEFAULT_WHITE, t, lastX, lastY + hOffset, 1.2, 2);
                lastX += this.rect.width * this.headersAndWidth.get(t);
                entriesWidth.add(this.headersAndWidth.get(t));
            }
            lastY += Font.DEFAULT_WHITE.getHeight() + 15;
            lastX = rect.x;
            
            this.orderTableByScore();
            
            // render the entries
            for (CopyOnWriteArrayList<String> wholeEntry : this.entries) {
                int i = -1;
                for (String entry : wholeEntry) {
                    if (i < 0) {
                        i++;
                        continue;
                    }
                    // draw cell
                    pix.drawRect(color, lastX, lastY, (int)(this.rect.width * entriesWidth.get(i)),
                            Font.DEFAULT_WHITE.getHeight() + 15);
                    // draw text
                    int hOffset = Font.DEFAULT_WHITE.getHeight();
                    // draw the characters of the string
                    pix.drawString(Font.DEFAULT_WHITE, entry, lastX, lastY + hOffset, 1.2, 2);
                    lastX += this.rect.width * entriesWidth.get(i);
                    i++;
                }
                lastY += Font.DEFAULT_WHITE.getHeight() + 15;
                lastX = rect.x;
            }
        }
    }
    
}
