package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;

public class Button extends GUIObject {
	private static final Color UP_COLOR = Color.LIGHT_GRAY;
	private static final Color DOWN_COLOR = Color.DARK_GRAY;
	private static final Color HOVER_COLOR = Color.WHITE;

	private SelectState state = SelectState.UP;
	
	public Color activeColor () {
		switch(state) {
			case UP:
				return UP_COLOR;

			case HOVER:
				return HOVER_COLOR;

			case DOWN:
				return DOWN_COLOR;
		}

		throw new IllegalStateException(state.toString() + "is not supported version of state");
	}
	
	public Button(int x, int y, int depth) {
		super(x, y, depth);
	}
	
	@Override
	void Draw(Graphics g) {
		g.setColor(activeColor());
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	@Override
	void OnClick() {
		System.out.println("Button clicked");
	}
	
	@Override
	void onMouseEnter() {
		state = SelectState.HOVER;
	}
	
	void onMouseOver() {
		if(state == SelectState.UP){
			state = SelectState.HOVER;
		}
	}
	
	@Override
	void OnMouseExit() {
		state = SelectState.UP;
	}
	
	@Override
	void onMouseDown() {
		state = SelectState.DOWN;
	}
	
	@Override
	void onMouseUp() {
		state = SelectState.UP;
	}
	
	@Override
	boolean isSelectable() {
		return true;
	}
}
