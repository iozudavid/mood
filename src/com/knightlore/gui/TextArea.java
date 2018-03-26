package com.knightlore.gui;

import java.awt.Color;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;

/**
 * Class used to create and render TextArea to render text on it with respect to
 * its height and width.
 *
 * @author David Iozu
 */
public class TextArea extends GUIObject {
    
    private final BlockingQueue<String> text;
    private int positionXToRender;
    private int positionYToRender;
    private boolean active = true;
    private boolean interactive = true;
    private Iterator<String> it = null;
    private Iterator<String> it2 = null;
    
    public TextArea(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = new LinkedBlockingQueue<>();
        this.positionXToRender = 0;
        this.positionYToRender = 0;
    }
    
    /**
     * Set if active. If not active then draw background transparent. If active
     * then draw background gray.
     *
     * @param b - set if active or not
     */
    public void setActive(boolean b) {
        this.active = b;
    }

    /**
     * Set if interactive or not. If interactive then render the text area. If
     * not, don't render it.
     *
     * @param b - set if interactive or not
     */
    public void setInteractive(boolean b) {
        this.interactive = b;
    }
    
    /**
     * Draw the text area and the text it has.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        if (!this.interactive) {
            return;
        }
        if (this.active) {
            int color = new Color(0x1F1F1F).getRGB();
            pix.fillRect(color, this.getRectangle().x, this.getRectangle().y, this.getRectangle().width,
                    this.getRectangle().height);
        }
        this.positionXToRender = (int)this.getRectangle().getX() + 1;
        this.positionYToRender = (int)this.getRectangle().getY() + Font.DEFAULT_WHITE.getHeight() + 5;
        char[] space = new char[1];
        space[0] = ' ';
        synchronized (this.text) {
            it = this.text.iterator();
            it2 = null;
            while (it.hasNext()) {
                if (it2 == null) {
                    it2 = this.text.iterator();
                    it2.next();
                }
                String currentText = it.next();
                for (String word : currentText.split(" ")) {
                    // draw words
                    this.fitText(word, pix, x, y);
                    // draw space
                    pix.drawString(Font.DEFAULT_WHITE, new String(space), positionXToRender, positionYToRender, 1, 2);
                    positionXToRender += pix.stringWidth(Font.DEFAULT_WHITE, " ", 1, 2);
                }
                // new line
                this.positionXToRender = (int)this.getRectangle().getX();
                this.positionYToRender += Font.DEFAULT_WHITE.getHeight() + 5;
            }
        }
    }
    
    /**
     * Choose if the text will fit in the space left. If not, new line. If
     * height full remove first message and redraw all messages as now there
     * will be space.
     *
     * @param word - word to be added
     * @param pix  - PixelBuffer to render on
     * @param x    - X position to start rendering from
     * @param y    - Y position to start rendering from
     */
    private void fitText(String word, PixelBuffer pix, int x, int y) {
        final int hOffset = Font.DEFAULT_WHITE.getHeight() + 5;
        final int wOffset = pix.stringWidth(Font.DEFAULT_WHITE, word, 1, 2);
        if (wOffset + this.positionXToRender < this.getRectangle().getWidth() &&
                this.positionYToRender < this.getRectangle().getHeight()) {
            // everything fits well
            // so just draw it
            pix.drawString(Font.DEFAULT_WHITE, word, this.positionXToRender, this.positionYToRender, 1, 2);
            this.positionXToRender += wOffset;
        } else if (hOffset + this.positionYToRender < this.getRectangle().getHeight()) {
            // width will exceed
            // so newline
            this.positionXToRender = (int)this.getRectangle().getX();
            this.positionYToRender += hOffset;
            //still bigger
            if (wOffset + this.positionXToRender > this.getRectangle().getWidth()) {
                //word is too big
                this.fitBigText(word, pix, x, y);
            } else {
                //everything good
                //draw it on the next line
                pix.drawString(Font.DEFAULT_WHITE, word, this.positionXToRender, this.positionYToRender, 1, 2);
                this.positionXToRender += wOffset;
            }
        } else {
            //textarea full
            //need to rerender everything without first message
            //in order to free some space
            it2.remove();
            this.Draw(pix, x, y);
        }
    }
    
    /**
     * If word too big to render in one empty line, Render as many chars as
     * possible and go to next line. If height full, remove first message and
     * redraw as now there will be enough height space.
     *
     * @param word - word to be added
     * @param pix  - PixelBuffer to render on
     * @param x    - X position to start rendering from
     * @param y    - Y position to start rendering from
     */
    private void fitBigText(String word, PixelBuffer pix, int x, int y) {
        char[] wordAsArray = word.toCharArray();
        for (char c : wordAsArray) {
            final int hOffset = Font.DEFAULT_WHITE.getHeight() + 5;
            final int wOffset = pix.stringWidth(Font.DEFAULT_WHITE, c + "", 1, 2);
            char[] charAsArray = new char[1];
            charAsArray[0] = c;
            if (wOffset + this.positionXToRender < this.getRectangle().getWidth() &&
                    this.positionYToRender < this.getRectangle().getHeight()) {
                // everything fits well
                // so just draw it
                pix.drawString(Font.DEFAULT_WHITE, new String(charAsArray), this.positionXToRender, this.positionYToRender, 1, 2);
                this.positionXToRender += wOffset;
            } else if (hOffset + this.positionYToRender < this.getRectangle().getHeight()) {
                // width will exceed
                // so newline
                this.positionXToRender = (int)this.getRectangle().getX();
                this.positionYToRender += hOffset;
                pix.drawString(Font.DEFAULT_WHITE, new String(charAsArray), this.positionXToRender, this.positionYToRender, 1, 2);
                this.positionXToRender += wOffset;
            } else {
                //textarea full
                //need to rerender everything without first message
                //in order to free some space
                it2.remove();
                this.Draw(pix, x, y);
                return;
            }
        }
    }
    
    /**
     * Add text to TextArea.
     *
     * @param newText - text to be added.
     */
    public void addText(String newText) {
        synchronized (this.text) {
            this.text.add(newText);
        }
    }
    
}
