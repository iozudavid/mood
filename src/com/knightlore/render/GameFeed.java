package com.knightlore.render;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.knightlore.engine.GameEngine;
import com.knightlore.gui.GameChat;
import com.knightlore.render.font.Font;

public class GameFeed implements IRenderable {

    private static GameFeed singleton = null;

    public static GameFeed getInstance() {
        if (singleton == null) {
            singleton = new GameFeed();
        }
        return singleton;
    }

    private List<GameFeedMessage> messages;
    private static final long MESSAGE_DURATION = (long) (GameEngine.UPDATES_PER_SECOND * 4);

    private GameFeed() {
        this.messages = new ArrayList<>();
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        for (GameFeedMessage msg : messages) {
            pix.drawString(Font.DEFAULT_WHITE, msg.message, x, y, 1, 2);
            y += 10;
        }
    }
    
    public void getFeed(GameChat chat) {
    	for(GameFeedMessage message: this.messages) {
    		chat.getTextArea().addText("System: " + message.message);
    	}
    	this.messages.clear();
    }

    public void update() {
        messages.removeIf((GameFeedMessage msg) -> msg.timeWhenGone <= GameEngine.ticker.getTime());
    }

    public void println(String str) {
        GameFeedMessage msg = new GameFeedMessage(str, GameEngine.ticker.getTime() + MESSAGE_DURATION);
        messages.add(msg);
    }

    private class GameFeedMessage {

        private String message;
        private long timeWhenGone;

        GameFeedMessage(String message, long timeWhenGone) {
            this.message = message;
            this.timeWhenGone = timeWhenGone;
        }

    }

}
