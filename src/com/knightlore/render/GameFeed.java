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
    private List<GameFeedMessage> damageMessages;
    private final long MESSAGE_DURATION = (long) (GameEngine.UPDATES_PER_SECOND * 4);

    private GameFeed() {
        this.messages = new ArrayList<GameFeedMessage>();
        this.damageMessages = new ArrayList<>();
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
		synchronized (damageMessages) {
			ListIterator<GameFeedMessage> itr = damageMessages.listIterator();
			while (itr.hasNext()) {
				GameFeedMessage msg = itr.next();
				pix.drawString(Font.DEFAULT_WHITE, msg.message, pix.getWidth() - 100, pix.getHeight() - 100, 3, 2);
				y += 10;
			}
		}
    }
    
    public void getFeed(GameChat chat){
		synchronized (messages) {
			for (GameFeedMessage message : this.messages) {
				chat.getTextArea().addText("System: " + message.message);
			}
			this.messages.clear();
		}
    }

    public void update() {
    	synchronized(messages){
    		messages.removeIf((GameFeedMessage msg) -> msg.timeWhenGone <= GameEngine.ticker.getTime());
    	}
		synchronized (damageMessages) {
			damageMessages.removeIf((GameFeedMessage msg) -> msg.timeWhenGone <= GameEngine.ticker.getTime());
		}
    }

    public void println(String str) {
		synchronized (messages) {
			GameFeedMessage msg = new GameFeedMessage(str, GameEngine.ticker.getTime() + MESSAGE_DURATION);
			messages.add(msg);
		}
    }
   
    public void printlnDamage(String str) {
    	synchronized(damageMessages){
    		GameFeedMessage msg = new GameFeedMessage(str, GameEngine.ticker.getTime()+50);
    		damageMessages.clear();
    		damageMessages.add(msg);
    	}
    }
    
    private class GameFeedMessage {

        private String message;
        private long timeWhenGone;

        public GameFeedMessage(String message, long timeWhenGone) {
            this.message = message;
            this.timeWhenGone = timeWhenGone;
        }

    }

}
