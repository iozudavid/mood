package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.funcptrs.VoidFunction;

public class Button extends GUIObject {

    public Button(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }
    
    public Button(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }
    
    public Button(int x, int y, int width, int height, String text, int fontSize){
    	this(x, y, width, height, 0, text, fontSize);
    }
    
    public Button(int x, int y, int width, int height, int depth, String text, int fontSize){
    	this(x, y, width, height, depth);
    	this.textArea = text;
    	this.fontSize=fontSize;
    }

    private String textArea;
    private int fontSize;

    private Graphic activeGraphic = null;
    
    private SelectState state = SelectState.UP_PHASE_1;
    
    // no harm in changing these externally
    public Color upColour1 = Color.LIGHT_GRAY;
    public Color upColour2 = Color.WHITE;
    public Color hoverColour1 = new Color(0,101,0);
    //lighter green
    public Color hoverColour2 = new Color(0, 221, 0);
    public Color downColour = Color.BLACK;
    
    
    public VoidFunction clickFunction;
    
    @SuppressWarnings("incomplete-switch")
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
        	
        }
        return upColour2;
      
    }
    
    
    
    @Override
    void Draw(Graphics g, Rectangle parentRect) {
        
        if (activeGraphic != null) {
        	
        } else {
        	g.setColor(Color.DARK_GRAY);
        	g.fillRect(rect.x-2, rect.y-2, rect.width+2, rect.height+2);
        	g.setColor(Color.BLACK);
        	g.fillRect(rect.x-1, rect.y-1, rect.width+1, rect.height+1);
            g.setColor(activeColor());
            System.out.println(activeColor());
            for(int x1=rect.x;x1<rect.width+rect.x;x1++){
            	for(int y1=rect.y;y1<rect.height+rect.y;y1++){
            		if(state==SelectState.HOVER_PHASE_1){
            			if(x1%2==0 || y1%2==0){
            				g.fillRect(x1, y1, 1, 1);
            			}
            		}
            		else if(state==SelectState.HOVER_PHASE_2){
            			if(!(x1%2==0 || y1%2==0)){
            				g.setColor(hoverColour2);
            			}else{
            				g.setColor(hoverColour1);
            			}
            			g.fillRect(x1, y1, 1, 1);
            		}
            		else if(state==SelectState.UP_PHASE_1){
            			if(x1%2==0 || y1%2==0){
            				g.fillRect(x1, y1, 1, 1);
            			}
            		}
            		else if(state==SelectState.UP_PHASE_2){
            			if(!(x1%2==0 || y1%2==0)){
            				g.setColor(upColour1);
            			}else{
            				g.setColor(upColour2);
            			}
            			g.fillRect(x1, y1, 1, 1);
            		}
            		else{
          				g.fillRect(x1, y1, 1, 1);
            		}
            	}
            }
            g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Bookman Old Style Bold", 10, fontSize));
            int width = g.getFontMetrics().stringWidth(textArea);
            int height = g.getFontMetrics().getHeight();
            width = rect.width-width;
            width /= 2f;
            height = (int) (rect.height/2f);
            height += fontSize/2;
            g.drawString(textArea, rect.x+width, rect.y+height);
            
        }
    }
    
    @Override
    void OnClick() {
        if (clickFunction == null) {
            return;
        }
        clickFunction.call();
    }
    
    @Override
    void onMouseEnter() {
    	if(state==SelectState.HOVER_PHASE_1)
    		state = SelectState.HOVER_PHASE_2;
    	else
    		state = SelectState.HOVER_PHASE_1;
    }
    
    void onMouseOver() {
    	if(state == SelectState.HOVER_PHASE_1)
    		state = SelectState.HOVER_PHASE_2;
    	else
    		state = SelectState.HOVER_PHASE_1;
    }
    
    @Override
    void OnMouseExit() {
    	if(state == SelectState.UP_PHASE_1)
    		state = SelectState.UP_PHASE_2;
    	else
    		state = SelectState.UP_PHASE_1;
    }
    
    @Override
    void onMouseDown() {
        state = SelectState.DOWN;
    }
    
    @Override
    void onMouseUp() {
    	if(state == SelectState.UP_PHASE_1)
    		state = SelectState.UP_PHASE_2;
    	else
    		state = SelectState.UP_PHASE_1;
    }
    
    @Override
    boolean isSelectable() {
        return true;
    }
}
