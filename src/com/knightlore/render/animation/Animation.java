package com.knightlore.render.animation;

import java.util.ArrayList;
import java.util.List;

import com.knightlore.render.graphic.Graphic;

public class Animation {

	private List<Graphic> frames;
	private int currentFrame;

	public Animation() {
		frames = new ArrayList<Graphic>();
		currentFrame = 0;
	}

	public void addFrame(Graphic g) {
		frames.add(g);
	}

	public Graphic getGraphic() {
		return frames.get(currentFrame);
	}

	public void nextFrame() {
		currentFrame = (currentFrame + 1) % frames.size();
	}

	public void prevFrame() {
		currentFrame = (currentFrame + 1) % frames.size();
	}

	public void setFrame(int frame) {
		currentFrame = frame;
	}

}
