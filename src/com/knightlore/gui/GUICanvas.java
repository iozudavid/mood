package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.funcptrs.VoidFunction;
import com.knightlore.utils.physics.Physics;

/**
 * The manager of all GUIObjects.
 * Keep them updated and rendered.
 *
 * @author David Iozu, James Adey
 */
public class GUICanvas extends GameObject implements IRenderable {
    
    private static final Color BACKGROUND_COLOR = new Color(0xFF00FF00, true);
    static TextField activeTextField;
    static TextField gameTextField;
    private static Optional<VoidFunction> onPressEscape = Optional.empty();
    private static Optional<VoidFunction> onPressQ = Optional.empty();
    private static Optional<VoidFunction> onReleaseQ = Optional.empty();
    public final boolean isVisible;
    private final int screenWidth;
    private final int screenHeight;
    private final List<GUIObject> guis;
    // the object that was selected when the mouse was pressed down
    private GUIObject downSelected;
    private GUIObject focussed;
    private GUIObject lastSelected;
    private boolean lastHeld;
    
    public GUICanvas(int screenWidth, int screenHeight) {
        super();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        guis = new ArrayList<>();
        isVisible = true;
    }
    
    /**
     * Called from the keyboard when a textual character is typed
     *
     * @param c - character typed
     */
    public static void inputChar(char c) {
        if (activeTextField != null) {
            activeTextField.onInputChar(c);
        }
    }
    
    /**
     * Called from the keyboard when a textual character is typed to the game
     * chat to be sent to the team
     *
     * @param c - character typed
     */
    public static void startMessageTeam(char c) {
        if (activeTextField == null) {
            if (gameTextField != null) {
                activeTextField = gameTextField;
                gameTextField.onMessage(c);
            }
        } else {
            inputChar(c);
        }
        
    }
    
    /**
     * Called from the keyboard when a textual character is typed to the game
     * chat to be sent to all players
     *
     * @param c - character typed
     */
    public static void startMessageAll(char c) {
        if (activeTextField == null) {
            if (gameTextField != null) {
                activeTextField = gameTextField;
                gameTextField.onMessage(c);
            }
        } else {
            inputChar(c);
        }
        
    }
    
    /**
     * Called from the keyboard when user wants to send the message to the game
     * chat.
     *
     * @param c - character typed
     */
    public static void sendMessage(char c) {
        if (gameTextField != null) {
            activeTextField = gameTextField;
            gameTextField.onSendMessage(c);
        }
    }
    
    /**
     * Called from the keyboard when user wants to exit the game chat.
     */
    public static void escape() {
        if (activeTextField != null) {
            activeTextField = null;
            gameTextField.escape();
        } else {
            onPressEscape.ifPresent(VoidFunction::call);
        }
    }
    
    /**
     * Called from the keyboard when Q button is pressed.
     * Call if any the function associated with this.
     */
    public static void pressQ() {
        onPressQ.ifPresent(VoidFunction::call);
    }
    
    /**
     * Called from the keyboard when Q button is released.
     * Call if any the function associated with this.
     */
    public static void releaseQ() {
        onReleaseQ.ifPresent(VoidFunction::call);
    }
    
    /**
     * Called from the keyboard when left arrow is pressed.
     * Move the mouse cursor to the left from TextField if is focused.
     */
    public static void inputLeftArrow() {
        if (activeTextField != null) {
            activeTextField.onLeftArrow();
        }
    }
    
    /**
     * Called from the keyboard when right arrow is pressed.
     * Move the mouse cursor to the right from TextField if is focused.
     */
    public static void inputRightArrow() {
        if (activeTextField != null) {
            activeTextField.onRightArrow();
        }
    }
    
    /**
     * Called from the keyboard when backspace is pressed. Delete a char from
     * the current cursor position from TextField if is focused.
     */
    public static void deleteChar() {
        if (activeTextField != null) {
            activeTextField.onDeleteChar();
        }
    }
    
    /**
     * @return whether the TextField is focused on not.
     */
    public static boolean isTyping() {
        return activeTextField != null;
    }
    
    /**
     * Set the current selected TextField from this GUICanvas if any.
     *
     * @param activeTextField - TextField to be set
     */
    public static void setActiveTextField(TextField activeTextField) {
        GUICanvas.activeTextField = activeTextField;
    }
    
    /**
     * Set a function for pressing Escape button from keyboard.
     *
     * @param func - function to be called on Escape
     */
    public static void setOnEscFunction(VoidFunction func) {
        onPressEscape = Optional.of(func);
    }
    
    /**
     * Set a function for pressing Q button from keyboard.
     *
     * @param func - function to be called on Q
     */
    public static void setOnQFunction(VoidFunction func) {
        onPressQ = Optional.of(func);
    }
    
    /**
     * Set a function for releasing Q button from keyboard.
     *
     * @param func - function to be called on releasing Q
     */
    public static void setOnQReleaseFunction(VoidFunction func) {
        onReleaseQ = Optional.of(func);
    }
    
    /**
     * Render all objects from this GUICanvas.
     */
    @Override
    public void render(PixelBuffer pix, int x, int y) {
        synchronized (guis) {
            for (GUIObject gui : guis) {
                gui.Draw(pix, x, y);
            }
        }
    }
    
    /**
     * Update the GUIObjects response depending on the mouse state.
     */
    @Override
    public void onUpdate() {
        Vector2D mousePos = InputManager.getMousePos();
        GUIObject selected = null;
        // linear reverse search
        synchronized (guis) {
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
    }
    
    @Override
    public void onDestroy() {
        synchronized (guis) {
            guis.clear();
        }
    }
    
    /**
     * Lower depths are first, therefore, draw in order.
     * Deeper things are drawn first
     */
    private void sort() {
        synchronized (guis) {
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
    }
    
    /**
     * Add a new GUIObject to this GUICanvas.
     *
     * @param gui - object to be added
     */
    public void addGUIObject(GUIObject gui) {
        synchronized (guis) {
            if (gui instanceof TextField) {
                gameTextField = (TextField)gui;
            }
            
            if (!guis.contains(gui)) {
                guis.add(gui);
                sort();
            }
        }
    }
    
    /**
     * Remove a GUIObject from this GUICanvas.
     *
     * @param gui - object to be removed.
     */
    public void removeGUIObject(GUIObject gui) {
        synchronized (guis) {
            guis.remove(gui);
        }
    }
    
}
