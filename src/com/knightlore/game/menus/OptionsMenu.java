package com.knightlore.game.menus;

import com.knightlore.gui.GUIObject;

public final class OptionsMenu {
    
    private boolean isOpen;
    private GUIObject menuPanel;
        
    public void setPanel(GUIObject p) {
        menuPanel = p;
    }
    
    public void closeMenu() {
        isOpen = false;
        menuPanel.isVisible = false;
    }
    
    public void openMenu() {
        isOpen = true;
        System.out.println("OPTIONS MENU OPENED");
        menuPanel.isVisible = true;
    }
    
    
}
