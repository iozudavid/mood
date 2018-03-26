package com.knightlore.gui;

import java.awt.Color;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.funcptrs.VoidFunction;

/**
 * Class creating a button with functionalities.
 * @author David Iozu, James Adey
 *
 */
public class Button extends GUIObject {

    public Button(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }

    public Button(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }

    public Button(int x, int y, int width, int height, String text, int fontSize) {
        this(x, y, width, height, 0, text, fontSize / 15D);
    }

    public Button(int x, int y, int width, int height, int depth, String text, double fontSize) {
        this(x, y, width, height, depth);
        this.textArea = text;
        this.fontSize = fontSize;
    }

    private String textArea;
    private double fontSize;

    private Graphic activeGraphic = null;
    private Graphic activeGraphic2 = null;

    public SelectState state = SelectState.UP_PHASE_1;

    // no harm in changing these externally
    public final Color upColour1 = new Color(177, 177, 177);
    public final Color upColour2 = new Color(191, 191, 191);
    public final Color hoverColour1 = new Color(209, 209, 209);
    public final Color hoverColour2 = new Color(230, 230, 230);
    public final Color downColour = new Color(250, 250, 250);

    public VoidFunction clickFunction;

    /**
     * 
     * @return the color depending of the object state.
     */
    public Color activeColor() {
        switch (state) {
        case UP_PHASE_1:
            return upColour1;

        case UP_PHASE_2:
            return hoverColour2;

        case HOVER_PHASE_1:
            return hoverColour1;

        case HOVER_PHASE_2:
            return hoverColour2;

        case DOWN:
            return downColour;

        default:
            return upColour2;
        }

    }

    /**
     * Set a graphic to be rendered to the left of the button.
     * 
     * @param g
     *            - graphic to be rendered to the left of the button
     */
    public void setGraphic(Graphic g) {
        this.activeGraphic = g;
    }

    /**
     * Set a graphic to be rendered to the right of the button.
     * 
     * @param g
     *            - graphic to be rendered to the right of the button
     */
    public void setGraphic2(Graphic g) {
        this.activeGraphic2 = g;
    }

    /**
     * Draw the button on the actual state (i.e hover) and graphics to the left
     * and right if any.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {

        int color;
        if (state != SelectState.UP_PHASE_1 && state != SelectState.UP_PHASE_2 && activeGraphic != null
                && activeGraphic.getImage() != null) {
            pix.drawGraphic(activeGraphic, (int) (rect.x - rect.getHeight() - 20), rect.y, 50, 50);
        }
        if (state != SelectState.UP_PHASE_1 && state != SelectState.UP_PHASE_2 && activeGraphic2 != null
                && activeGraphic2.getImage() != null) {
            pix.drawGraphic(activeGraphic2, (int) (rect.x + rect.getWidth() + 10), rect.y, 50, 50);
        }
        color = Color.DARK_GRAY.getRGB();
        pix.fillRect(color, rect.x - 2, rect.y - 2, rect.width + 2, rect.height + 2);
        color = Color.BLACK.getRGB();
        pix.fillRect(color, rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1);
        color = activeColor().getRGB();
        for (int x1 = rect.x; x1 < rect.width + rect.x; x1++) {
            for (int y1 = rect.y; y1 < rect.height + rect.y; y1++) {
                switch (state) {
                    case HOVER_PHASE_1:
                        if (x1 % 2 == 0 || y1 % 2 == 0) {
                            pix.fillRect(color, x1, y1, 1, 1);
                        }
                        break;
                    case HOVER_PHASE_2:
                        if (!(x1 % 2 == 0 || y1 % 2 == 0)) {
                            color = hoverColour2.getRGB();
                        } else {
                            color = hoverColour1.getRGB();
                        }
                        pix.fillRect(color, x1, y1, 1, 1);
                        break;
                    case UP_PHASE_1:
                        if (x1 % 2 == 0 || y1 % 2 == 0) {
                            pix.fillRect(color, x1, y1, 1, 1);
                        }
                        break;
                    case UP_PHASE_2:
                        if (!(x1 % 2 == 0 || y1 % 2 == 0)) {
                            color = upColour2.getRGB();
                        } else {
                            color = upColour1.getRGB();
                        }
                        pix.fillRect(color, x1, y1, 1, 1);
                        break;
                    default:
                        pix.fillRect(color, x1, y1, 1, 1);
                        break;
                }
            }
        }
        int width = rect.width - pix.stringWidth(Font.DEFAULT_WHITE, textArea, this.fontSize, 2);
        width /= 2f;
        int height = (int) (rect.height / 2f);
        height += fontSize / 2;
        pix.drawString(Font.DEFAULT_WHITE, textArea, rect.x + width, rect.y + height, this.fontSize, 2);

    }

    /**
     * Applies the function set for this button when clicked
     */
    @Override
    void OnClick() {
        if (clickFunction == null) {
            return;
        }
        clickFunction.call();

    }

    /**
     * Change the state to hover.
     */
    @Override
    void onMouseEnter() {
        state = SelectState.HOVER_PHASE_2;
    }

    /**
     * Change the state to hover.
     */
    void onMouseOver() {
        state = SelectState.HOVER_PHASE_2;
    }

    /**
     * Change the state to up.
     */
    @Override
    void OnMouseExit() {
        state = SelectState.UP_PHASE_1;
    }

    /**
     * Change the state to up.
     */
    @Override
    void onMouseDown() {
        state = SelectState.UP_PHASE_2;
    }

    /**
     * Change the state to hover.
     */
    @Override
    void onMouseUp() {
        if (state == SelectState.UP_PHASE_1) {
            state = SelectState.UP_PHASE_2;
        } else {
            state = SelectState.UP_PHASE_1;
        }
    }

    @Override
    boolean isSelectable() {
        return true;
    }
}
