package com.knightlore.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Physics;
import com.knightlore.utils.Vector2D;

public class GUICanvas extends GameObject implements IRenderable {
    // TODO remove this and make it screen size
    private static final int TEMP_SIZE = 200;
    private static final Color BACKGROUND_COLOR = new Color(0xFF000000, true);

    private static TextField activeTextField;

    private final List<GUIObject> guis = new ArrayList<>();
    private final Graphic canvasGraphic;
    private final BufferedImage canvasImage;
    private final Graphics2D canvasG2D;
    private final int[] drawPixels;

    // the object that was selected when the mouse was pressed down
    private GUIObject downSelected;
    private GUIObject focussed;
    private GUIObject lastSelected;
    private boolean lastHeld;

    public GUICanvas() {
        super();
        canvasImage = new BufferedImage(TEMP_SIZE, TEMP_SIZE, BufferedImage.TYPE_INT_ARGB);
        canvasG2D = canvasImage.createGraphics();
        canvasG2D.setComposite(AlphaComposite.SrcOver);
        canvasGraphic = new Graphic(canvasImage);
        // store pixel array
        drawPixels = canvasGraphic.getPixels();
        canvasG2D.setFont(Font.getFont(Font.SERIF));
    }

    /**
     * Called from the keyboard when a textual character is typed
     */
    public static void inputChar(char c) {
        if (activeTextField != null) {
            activeTextField.onInputChar(c);
        }
    }

    public static void inputLeftArrow() {
        if (activeTextField != null) {
            activeTextField.onLeftArrow();
        }
    }

    public static void inputRightArrow() {
        if (activeTextField != null) {
            activeTextField.onRightArrow();
        }
    }

    public static boolean isTyping() {
        return activeTextField != null;
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        canvasG2D.setColor(BACKGROUND_COLOR);
        canvasG2D.fillRect(x, y, TEMP_SIZE, TEMP_SIZE);
        for (GUIObject gui : guis) {
            gui.Draw(canvasG2D);
        }

        canvasImage.getRGB(0, 0, TEMP_SIZE, TEMP_SIZE, drawPixels, 0, TEMP_SIZE);
        pix.drawGraphic(canvasGraphic, x, y);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onUpdate() {
        Vector2D mousePos = InputManager.getMousePos();
        GUIObject selected = null;
        // linear reverse search
        for (int i = guis.size() - 1; i >= 0; i--) {
            GUIObject gui = guis.get(i);
            // skip those that aren't selectable
            if (!gui.isSelectable()) {
                continue;
            }

            if (Physics.pointInAWTRectangleTest(mousePos, gui.rect)) {
                selected = gui;
                break;
            }
        }

        // notify previous gui of mouse exit
        if (lastSelected != selected) {
            if (lastSelected != null) {
                if (downSelected == lastSelected) {
                    lastSelected.onMouseUp();
                }
                lastSelected.OnMouseExit();
            }
        }

        // notify new gui of mouse enter
        if (selected != null) {
            // send mouse entered event
            if (selected != lastSelected) {
                selected.onMouseEnter();
            }

            selected.onMouseOver();

            // mouse held and we
            if (InputManager.getMouse().isLeftHeld() && !lastHeld) {
                // send mouse down event
                selected.onMouseDown();
                downSelected = selected;
            } else if (!InputManager.getMouse().isLeftHeld() && lastHeld) {
                // did we click down over the same object
                if (downSelected == selected) {
                    selected.OnClick();
                    if (focussed != selected && focussed != null) {
                        focussed.onLostFocus();
                    }

                    focussed = selected;
                    selected.onGainedFocus();
                }
                // send mouse up event
                selected.onMouseUp();
            }
        } else {
            if (!InputManager.getMouse().isLeftHeld() && lastHeld) {
                // we have nothing selected... did we click?
                if (focussed != null) {
                    focussed.onLostFocus();
                    focussed = null;
                }
            }
        }
        lastHeld = InputManager.getMouse().isLeftHeld();
        lastSelected = selected;
    }

    @Override
    public void onDestroy() {
        canvasG2D.dispose();
    }

    private void sort() {
        for (int i = 1; i < guis.size(); i++) {
            GUIObject left = guis.get(i);
            GUIObject right = guis.get(i - 1);
            // swap
            if (left.depth < right.depth) {
                guis.set(i, right);
                guis.set(i - 1, left);
            }
        }
    }

    public void addGUIObject(GUIObject gui) {
        guis.add(gui);
        sort();
    }

    public void removeGUIObject(GUIObject gui) {
        guis.remove(gui);
    }

    public static void setActiveTextField(TextField activeTextField) {
        GUICanvas.activeTextField = activeTextField;
    }
}
