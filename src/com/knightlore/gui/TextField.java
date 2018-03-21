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

public class TextField extends GUIObject {
    private static final Color upColour = Color.WHITE;
    private static final Color downColour = Color.LIGHT_GRAY;
    private static final Color hoverColour = Color.GRAY;
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

    TextField(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }

    public TextField(int x, int y, int width, int height, String text) {
        this(x, y, width, height, 0, text);
    }

    public void setText(String newText) {
        text = newText;
        displayText(text);
    }

    public TextField(int x, int y, int width, int height, int depth, String text) {
        super(x, y, width, height, depth);
        setText(text);
        displayText(text);
    }

    public String getText() {
        return text;
    }

    public void setRestriction(BooleanFunction<Character> restrict) {
        this.restrictText = Optional.of(restrict);
    }

    public void setRestrictionLength(BooleanFunction<Integer> restrict) {
        this.restrictTextLength = Optional.of(restrict);
    }

    public void setRestrictionValue(Predicate<String> restrict) {
        this.restrictValue = Optional.of(restrict);
    }

    public void displayText(String t) {
        rawChars = t.toCharArray();
    }

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

    @Override
    void Draw(PixelBuffer pix, int x, int y) {
        if ((GameEngine.getSingleton().guiState == GUIState.InGame) && GUICanvas.activeTextField == null) {
            return;
        }
        // draw a background
        int color = Color.DARK_GRAY.getRGB();
        pix.fillRect(color, rect.x - 2, rect.y - 2, rect.width + 2, rect.height + 2);
        color = Color.BLACK.getRGB();
        pix.fillRect(color, rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1);
        color = activeColor().getRGB();
        int hOffset = Font.DEFAULT_WHITE.getHeight();
        pix.fillRect(color, rect.x, rect.y, rect.width, rect.height);
        // draw the characters of the string
        color = Color.BLACK.getRGB();

        if (text != null) {
            int width = pix.stringWidth(Font.DEFAULT_WHITE, rawChars.toString(), 1, 2);
            if (width < this.rect.width) {
                pix.drawString(Font.DEFAULT_WHITE, new String(rawChars), rect.x, rect.y, 1, 2);
            } else {
                width = pix.stringWidth(Font.DEFAULT_WHITE, rawChars.toString(), 15, 2);
                char[] toDisplay = rawChars;
                while (width > this.rect.width) {
                    toDisplay = new char[toDisplay.length - 1];
                    for (int i = toDisplay.length - 1; i >= 0; i--) {
                        int j = (toDisplay.length - 1) - i;
                        System.out.println(toDisplay.length);
                        System.out.println(rawChars.length);
                        toDisplay[i] = rawChars[rawChars.length - 1 - j];
                    }
                    width = pix.stringWidth(Font.DEFAULT_WHITE, toDisplay.toString(), 15, 2);
                }
                pix.drawString(Font.DEFAULT_WHITE, toDisplay.toString(), rect.x, rect.y + hOffset, 15, 2);

            }
        }
        this.pix = pix;
    }

    @Override
    boolean isSelectable() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    void onGainedFocus() {
        System.out.println("GAINED FOCUS");
        GUICanvas.setActiveTextField(this);

    }

    @Override
    void onLostFocus() {
        System.out.println("LOST FOCUS");
        GUICanvas.activeTextField = null;
        if (this.restrictValue.isPresent()) {
            if (!this.restrictValue.get().test(text)) {
                if (this.text.length() == 0) {
                    text = "10";
                } else if (Integer.parseInt(this.text) < 5)
                    text = "5";
                else
                    text = "20";

            }
        }
        displayText(text);
    }

    void onInputChar(char c) {
        if (text == null) {
            System.err.println("tried to insert char into null string");
            text = "";
            return;
        }
        if (this.restrictText.isPresent()) {
            if (!this.restrictText.get().check(c))
                return;
        }

        if (this.restrictTextLength.isPresent()) {
            if (!this.restrictTextLength.get().check(text.length() + 1))
                return;
        }

        if (text.isEmpty()) {
            insertString = c + "|";
        } else {
            insertString = text.substring(0, insertPosition) + c + '|' + text.substring(insertPosition);
        }
        text = insertString.replace("|", "");
        insertPosition++;
        displayText(insertString);
    }

    void onMessage(char c) {
        this.sendTo = c;
        if (text == null || text.isEmpty()) {
            insertString = "|";
        } else {
            insertString = text.substring(0, insertPosition) + '|' + text.substring(insertPosition);
        }
        text = insertString.replace("|", "");
        displayText(insertString);
    }

    void onSendMessage(char c) {
        // do not send if null or nothing to send
        if (text == null || text.length() == 0) {
            return;
        }
        ClientNetworkObjectManager manager = (ClientNetworkObjectManager) GameEngine.getSingleton()
                .getNetworkObjectManager();
        ByteBuffer nextMessage = constructMessage(manager.getMyPlayer().getObjectId());
        manager.addToChat(nextMessage);
        this.insertPosition = 0;
        this.insertString = "|";
        this.text = this.insertString.replaceAll("|", "");
        displayText(insertString);
    }

    void escape() {
        this.insertPosition = 0;
        this.insertString = "";
        this.text = this.insertString.replaceAll("|", "");
        displayText(insertString);
    }

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

    void onLeftArrow() {
        insertPosition--;
        if (insertPosition < 0) {
            insertPosition = 0;
        }

        insertString = text.substring(0, insertPosition) + '|' + text.substring(insertPosition);
        displayText(insertString);
    }

    void onRightArrow() {
        insertPosition++;
        if (insertPosition > text.length()) {
            insertPosition = text.length();
        }
        insertString = text.substring(0, insertPosition) + '|' + text.substring(insertPosition);
        displayText(insertString);
    }

    void onDeleteChar() {
        if (text.isEmpty() || insertPosition == 0) {
            return;
        } else if (insertPosition >= text.length()) {
            insertPosition = text.length();
            if (text.length() == 1) {
                insertPosition = 0;
                insertString = "|";
            } else {
                insertString = text.substring(0, insertPosition - 1) + '|';
                insertPosition--;
            }
            displayText(insertString);
        } else {
            insertString = text.substring(0, insertPosition - 1) + '|' + text.substring(insertPosition);
            insertPosition--;
            displayText(insertString);
        }
        text = insertString.replace("|", "");
    }

    @Override
    void onMouseEnter() {
        state = SelectState.HOVER;
    }

    void onMouseOver() {
        if (state == SelectState.UP) {
            state = SelectState.HOVER;
        }
    }

    @Override
    void OnMouseExit() {
        state = SelectState.UP;
    }

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
                int width = pix.stringWidth(Font.DEFAULT_WHITE, c + "", 15, 2);
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
        insertString = text.substring(0, chooseLocation) + "|" + text.substring(chooseLocation);
        displayText(insertString);
        state = SelectState.DOWN;
    }

    @Override
    void onMouseUp() {
        state = SelectState.UP;
    }
}
