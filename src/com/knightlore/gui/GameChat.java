package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.manager.GameManager;
import com.knightlore.game.manager.GameState;
import com.knightlore.network.client.ClientManager;
import com.knightlore.render.PixelBuffer;

/**
 * Class rendering the game chat.
 * @author David Iozu
 *
 */
public class GameChat extends GUICanvas {
    private TextArea textArea;
    private TextField textField;
    private PixelBuffer pix;
    private final int screenWidth;
    private final int screenHeight;
    private int count = 0;
    private boolean interactive = true;
    private final int timeToInteractive = 200;
    private Image pauseImage;
    private Button resumeButton;
    private Button mainMenuButton;
    private Button exitButton;
    private boolean lastPauseVisible = false;
    private boolean lastScoreVisible = false;
    private Image scoreBoardImage;
    private Table scoreBoard;
    private Text timeLeftText;

    public GameChat(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);
        System.out.println("CHATTTTTTTTTTTTTTTTT");
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        // shouldn't be in constructor... but damn race conditions
        init();
    }

    @Override
    public void init() {
        super.init();
        this.pix = new PixelBuffer(screenWidth, screenHeight);
        this.pix.flood(-16711936);
        this.textArea = new TextArea(0, 0, (int) (screenWidth * 0.3), (int) (screenHeight * 0.3));
        this.textField = new TextField(0,
                (int) (this.textArea.getRectangle().getY() + this.textArea.getRectangle().getHeight()),
                (int) (screenWidth * 0.3), (int) (screenHeight * 0.05));
        this.textField.fontSize = 2;
        this.textField.setSelect(false);
        this.addGUIObject(this.textArea);
        this.addGUIObject(this.textField);
        this.textArea.addText("System: access team chat by pressing t");
        this.textArea.addText("System: access all chat by pressing y");
        this.textArea.addText("System: exit chat by pressing esc");
        this.textArea.addText("System: acces pause menu by pressing esc");
        this.textArea.addText("System: acces scoreboard menu by pressing and holding q");
        this.pauseImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 15),
                150, 50, "res/graphics/Pause.png");
        pauseImage.needBackground = false;
        this.scoreBoardImage = new Image(GuiUtils.middleWidth(screenWidth, 150),
                GuiUtils.calculateHeight(screenHeight, 10), 150, 50, "res/graphics/ScoreBoard.png");
        scoreBoardImage.needBackground = false;
        this.scoreBoard = new Table(GuiUtils.middleWidth(screenWidth, (int) (screenWidth * 0.66)),
                GuiUtils.calculateHeight(screenHeight, 20), (int) (screenWidth * 0.66), 20, 0);
        ArrayList<String> header = new ArrayList<>();
        header.add("Player Name");
        header.add("Team");
        header.add("Score");
        this.scoreBoard.setTableHeader(header);
        timeLeftText = new Text(GuiUtils.middleWidth(screenWidth, 128), 0, 128, 30, null, 30);
        timeLeftText.currentColor = Color.WHITE;
        this.addGUIObject(timeLeftText);
        timeLeftText.SetText("00:00");
        GUICanvas.setOnEscFunction(() -> GameChat.this.setPauseMenuVisible(!GameChat.this.lastPauseVisible));

        GUICanvas.setOnQFunction(() -> {
            if (GameChat.this.lastScoreVisible) {
                return;
            }
            GameChat.this.lastScoreVisible = true;
            GameChat.this.setScoreMenuVisible();
        });

        GUICanvas.setOnQReleaseFunction(() -> {
            if (!GameChat.this.lastScoreVisible) {
                return;
            }
            GameChat.this.lastScoreVisible = false;
            GameChat.this.setScoreMenuVisible();
        });
    }

    /**
     * Hide the pause menu when we don't want it to be shown.
     */
    private void initPauseHidden() {
        this.resumeButton = new Button(GuiUtils.middleWidth(screenWidth, 300),
                GuiUtils.calculateHeight(screenHeight, 30), 300, 40, "Resume", 20);
        this.mainMenuButton = new Button(GuiUtils.middleWidth(screenWidth, 300),
                GuiUtils.calculateHeight(screenHeight, 40), 300, 40, "Main Menu", 20);
        this.exitButton = new Button(GuiUtils.middleWidth(screenWidth, 300), GuiUtils.calculateHeight(screenHeight, 50),
                300, 40, "Exit", 20);
        this.resumeButton.clickFunction = () -> GameChat.this.setPauseMenuVisible(false);
        this.mainMenuButton.clickFunction = () -> {
            GameEngine.getSingleton().stopGame();
            ClientManager.disconnect();
            GameChat.this.destroy();
            GameEngine.getSingleton().guiState = GUIState.StartMenu;
        };
        this.exitButton.clickFunction = () -> {
            ClientManager.disconnect();
            GameChat.this.destroy();
            GameEngine.getSingleton().stop();
        };
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        if (GUICanvas.activeTextField != null) {
            this.interactive = true;
            count = 0;
        } else if (count > this.timeToInteractive) {
            this.interactive = false;
            count = 0;
        }
        if (this.interactive) {
            count++;
        }
        this.textArea.setActive(GUICanvas.activeTextField != null);
        if (GUICanvas.activeTextField != null) {
            this.textArea.setInteractive(true);
        } else {
            this.textArea.setInteractive(this.interactive);
        }
        super.render(pix, x, y);
    }

    /**
     * 
     * @return PixelBuffer we render on
     */
    public PixelBuffer getPixelBuffer() {
        PixelBuffer copy = this.pix;
        this.pix = new PixelBuffer(screenWidth, screenHeight);
        this.pix.flood(-16711936);
        return copy;
    }

    /**
     * 
     * @return TextArea we show the messages
     */
    public TextArea getTextArea() {
        this.interactive = true;
        this.count = 0;
        return this.textArea;
    }

    /**
     * Add entry to scoreboard.
     * 
     * @param entry
     *            - entity stats to be added in scoreboard
     */
    public void addToTable(CopyOnWriteArrayList<String> entry) {
        this.scoreBoard.addTableEntry(entry);
    }

    /**
     * Remove entry from scoreboard.
     *
     */
    public void removeFromTable(String uuid) {
        this.scoreBoard.removeTableEntry(uuid);
    }

    /**
     * Show/hide pause menu when user wants it.
     * 
     * @param b
     *            - whether show or hide the pause menu
     */
    private void setPauseMenuVisible(boolean b) {
        this.lastPauseVisible = b;
        if (b) {
            this.initPauseHidden();
            this.addGUIObject(pauseImage);
            this.addGUIObject(resumeButton);
            this.addGUIObject(mainMenuButton);
            this.addGUIObject(exitButton);
        } else {
            this.removeGUIObject(pauseImage);
            this.removeGUIObject(resumeButton);
            this.removeGUIObject(mainMenuButton);
            this.removeGUIObject(exitButton);
            this.resumeButton = null;
            this.mainMenuButton = null;
            this.exitButton = null;
        }
    }

    /**
     * Show the scoreboard when user wants it to.
     */
    public void setScoreMenuVisible() {
        if (this.lastScoreVisible || GameManager.getGameState() == GameState.FINISHED) {
            this.addGUIObject(scoreBoardImage);
            this.addGUIObject(scoreBoard);
        } else {
            this.removeGUIObject(scoreBoardImage);
            this.removeGUIObject(scoreBoard);
        }
    }

    /*
     * Set the game's time left.
     */
    public void setTimeLeft(String time) {
        timeLeftText.SetText(time);
    }

}
