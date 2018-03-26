package com.knightlore.gui;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.input.InputManager;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.funcptrs.BooleanFunction;

/**
 * Class used to create and render TextField which let user to insert text with respect to its width.
 *
 * @author David Iozu, James Adey.
 */
public class TextField extends GUIObject {
    
    private static final String CURSOR = "_";
    
    private static final Color upColour = Color.WHITE;
    private static final Color downColour = Color.LIGHT_GRAY;
    private static final Color hoverColour = Color.GRAY;
    public final Font font = Font.DEFAULT_WHITE;
    public int fontSize = 1;
    private PixelBuffer pix;
    private SelectState state = SelectState.UP;
    private String text = "";
    private String insertString = "";
    private char[] rawChars = new char[0];
    private int insertPosition = 0;
    private char sendTo;
    private boolean select = true;
    private Optional<BooleanFunction<Character>> restrictText = Optional.empty();
    private Optional<BooleanFunction<Integer>> restrictTextLength = Optional.empty();
    private Optional<Predicate<String>> restrictValue = Optional.empty();
    
    public TextField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public TextField(int x, int y, int width, int height, String text) {
        this(x, y, width, height, 0, text);
    }
    
    private TextField(int x, int y, int width, int height, int depth, String text) {
        super(x, y, width, height, depth);
        setText(text);
        displayText(text);
    }
    
    /**
     * @return current text rendered on this TextField
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set a text on this TextField.
     *
     * @param newText - text to set on TextField
     */
    public void setText(String newText) {
        text = newText;
        displayText(text);
    }
    
    /**
     * Set a restriction of characters inserted for this TextField.
     *
     * @param restrict - function to be applied
     */
    public void setRestriction(BooleanFunction<Character> restrict) {
        this.restrictText = Optional.of(restrict);
    }
    
    /**
     * Set a restriction of text length for this TextField.
     *
     * @param restrict - function to be applied
     */
    public void setRestrictionLength(BooleanFunction<Integer> restrict) {
        this.restrictTextLength = Optional.of(restrict);
    }
    
    /**
     * Dispay the given text.
     *
     * @param t - text to be displayed
     */
    public void displayText(String t) {
        synchronized (this.rawChars) {
            rawChars = t.toCharArray();
        }
    }
    
    /**
     * @return the appropriate color for current state
     */
    public Color activeColor() {
        switch (state) {
            case UP:
                return upColour;
            
            case HOVER:
                return hoverColour;
            
            case DOWN:
                return downColour;
        }
        
        throw new IllegalStateException("State " + state + " is not legal");
    }
    
    /**
     * Draw the TextField with the current text inserted. If text go beyond the
     * width limit, then shift the view to the right to let the last character
     * inserted to be seen. Also, calculates from which character to start
     * rendering when the text is too big to fit the width.
     */
    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        if ((GameEngine.getSingleton().guiState == GUIState.IN_GAME) && GUICanvas.activeTextField == null) {
            return;
        }
        synchronized (this.rawChars) {
            // draw a background
            int color = Color.DARK_GRAY.getRGB();
            pix.fillRect(color, rect.x - 2, rect.y - 2, rect.width + 2, rect.height + 2);
            color = Color.BLACK.getRGB();
            pix.fillRect(color, rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1);
            color = activeColor().getRGB();
            int hOffset = font.getHeight();
            pix.fillRect(color, rect.x, rect.y, rect.width, rect.height);
            
            Font font = state == SelectState.UP ? Font.DEFAULT_BLACK : Font.DEFAULT_WHITE;
            
            if (text != null) {
                int width = pix.stringWidth(font, new String(rawChars), this.fontSize / 1.5, 2);
                if (width < this.rect.width) {
                    pix.drawString(font, new String(rawChars), rect.x + 2, rect.y + hOffset, this.fontSize / 1.5, 2);
                } else {
                    width = pix.stringWidth(font, new String(rawChars), this.fontSize / 1.5, 2);
                    char[] toDisplay = rawChars;
                    while (width > this.rect.width) {
                        toDisplay = new char[toDisplay.length - 1];
                        for (int i = toDisplay.length - 1; i >= 0; i--) {
                            int j = (toDisplay.length - 1) - i;
                            System.out.println(toDisplay.length);
                            System.out.println(rawChars.length);
                            toDisplay[i] = rawChars[rawChars.length - 1 - j];
                        }
                        width = pix.stringWidth(font, new String(toDisplay), this.fontSize / 1.5, 2);
                    }
                    pix.drawString(font, new String(toDisplay), rect.x + 2, rect.y + hOffset, this.fontSize / 1.5, 2);
                    
                }
            }
            this.pix = pix;
        }
    }
    
    @Override
    boolean isSelectable() {
        return select;
    }
    
    /**
     * Set if selectable or not.
     *
     * @param select - whether can be selectablee or not
     */
    public void setSelect(boolean select) {
        this.select = select;
    }
    
    /**
     * Called when TextField is accessed.
     */
    @Override
    void onGainedFocus() {
        System.out.println("GAINED FOCUS");
        GUICanvas.setActiveTextField(this);
        
    }
    
    /**
     * Called when TextField lost the focus.
     */
    @Override
    void onLostFocus() {
        System.out.println("LOST FOCUS");
        GUICanvas.activeTextField = null;
        this.restrictValue.ifPresent(stringPredicate -> {
            if (this.text.isEmpty()) {
                text = "10";
            }
            if (!stringPredicate.test(text)) {
                if (this.text.isEmpty()) {
                    text = "10";
                } else if (Integer.parseInt(this.text) < 5) {
                    text = "5";
                } else {
                    text = "20";
                }
                
            }
        });
        displayText(text);
    }
    
    /**
     * Draw the char which was inserted from the keyboard. and move the position
     * of the cursor.
     *
     * @param c - character to be inserted
     */
    void onInputChar(char c) {
        if (text == null) {
            System.err.println("tried to insert char into null string");
            text = "";
            return;
        }
        if (this.restrictText.isPresent()) {
            if (!this.restrictText.get().check(c)) {
                return;
            }
        }
        
        if (this.restrictTextLength.isPresent()) {
            if (!this.restrictTextLength.get().check(text.length() + 1)) {
                return;
            }
        }
        
        if (text.isEmpty()) {
            insertString = c + CURSOR;
        } else {
            insertString = text.substring(0, insertPosition) + c + CURSOR + text.substring(insertPosition);
        }
        text = insertString.replace(CURSOR, "");
        insertPosition++;
        displayText(insertString);
    }
    
    /**
     * Called when game chat is activated. Initialized the text as empty and
     * take the type of the message(i.e t for team, y for all)
     *
     * @param c - type of game chat
     */
    void onMessage(char c) {
        this.sendTo = c;
        if (text == null || text.isEmpty()) {
            insertString = CURSOR;
        } else {
            insertString = text.substring(0, insertPosition) + CURSOR + text.substring(insertPosition);
        }
        text = insertString.replace(CURSOR, "");
        displayText(insertString);
    }
    
    /**
     * Called when user wants to send a message on game chat. Send to the server
     * and erase the text.
     *
     */
    void onSendMessage() {
        // do not send if null or nothing to send
        if (text == null || text.isEmpty()) {
            return;
        }
        ClientNetworkObjectManager manager = (ClientNetworkObjectManager)GameEngine.getSingleton()
                .getNetworkObjectManager();
        ByteBuffer nextMessage = constructMessage(manager.getMyPlayer().getObjectId());
        manager.addToChat(nextMessage);
        this.insertPosition = 0;
        this.insertString = CURSOR;
        this.text = this.insertString.replaceAll(CURSOR, "");
        displayText(insertString);
    }
    
    /**
     * Called when player wants to lost focus on the game chat.
     * Erase the text.
     */
    void escape() {
        this.insertPosition = 0;
        this.insertString = "";
        this.text = this.insertString.replaceAll(CURSOR, "");
        displayText(insertString);
    }
    
    /**
     * Construct a packet to be sent via networking.
     *
     * @param uuid - user UUID to let other players know where the message comes
     *             from
     * @return the packet to be sent
     */
    public ByteBuffer constructMessage(UUID uuid) {
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, uuid.toString());
        if (this.sendTo == 't') {
            NetworkUtils.putStringIntoBuf(bf, "messageToTeam");
        } else {
            NetworkUtils.putStringIntoBuf(bf, "messageToAll");
        }
        NetworkUtils.putStringIntoBuf(bf, this.text);
        return bf;
    }
    
    /**
     * Move the cursor to the left.
     */
    void onLeftArrow() {
        insertPosition--;
        if (insertPosition < 0) {
            insertPosition = 0;
        }
        
        insertString = text.substring(0, insertPosition) + CURSOR + text.substring(insertPosition);
        displayText(insertString);
    }
    
    /**
     * Move the cursor to the right.
     */
    void onRightArrow() {
        insertPosition++;
        if (insertPosition > text.length()) {
            insertPosition = text.length();
        }
        insertString = text.substring(0, insertPosition) + CURSOR + text.substring(insertPosition);
        displayText(insertString);
    }
    
    /**
     * Delete the char which is to the left of the cursor.
     */
    void onDeleteChar() {
        if (text.isEmpty() || insertPosition == 0) {
            return;
        } else if (insertPosition >= text.length()) {
            insertPosition = text.length();
            if (text.length() == 1) {
                insertPosition = 0;
                insertString = CURSOR;
            } else {
                insertString = text.substring(0, insertPosition - 1) + CURSOR;
                insertPosition--;
            }
            displayText(insertString);
        } else {
            insertString = text.substring(0, insertPosition - 1) + CURSOR + text.substring(insertPosition);
            insertPosition--;
            displayText(insertString);
        }
        text = insertString.replace(CURSOR, "");
    }
    
    /**
     * Set the state to hover.
     */
    @Override
    void onMouseEnter() {
        state = SelectState.HOVER;
    }
    
    /**
     * Set the state to hover.
     */
    void onMouseOver() {
        if (state == SelectState.UP) {
            state = SelectState.HOVER;
        }
    }
    
    /**
     * Set the state to up.
     */
    @Override
    void OnMouseExit() {
        state = SelectState.UP;
    }
    
    /**
     * When click gain focus on this TextField. It gets the mouse position and
     * insert the cursor in that position if there is text. If the mouse is
     * after text ending then insert it after the last character.
     */
    @Override
    void onMouseDown() {
        Vector2D mousePos = InputManager.getMousePos();
        double deltaPos = mousePos.getX() - this.rect.x;
        int chooseLocation = 0;
        if (this.text == null) {
            this.text = "";
            this.insertPosition = 0;
            
        } else {
            for (char c : this.text.toCharArray()) {
                chooseLocation++;
                int width = pix.stringWidth(font, c + "", this.fontSize, 2);
                double oldDelta = deltaPos;
                deltaPos -= width;
                if (deltaPos < 0) {
                    if (oldDelta < Math.abs(deltaPos)) {
                        this.insertPosition = --chooseLocation;
                    } else {
                        this.insertPosition = chooseLocation;
                    }
                    break;
                }
            }
            if (deltaPos > 0) {
                this.insertPosition = text.length();
            }
        }
        insertString = text.substring(0, chooseLocation) + CURSOR + text.substring(chooseLocation);
        displayText(insertString);
        state = SelectState.DOWN;
    }
    
    /**
     * Set the state to up.
     */
    @Override
    void onMouseUp() {
        state = SelectState.UP;
    }
}
