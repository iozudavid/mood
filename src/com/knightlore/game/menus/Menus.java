package com.knightlore.game.menus;

public final class Menus {
    
    // TODO add more menus
    private static OptionsMenu optionsMenu = null;
    private static boolean hasMenus = false; 
    
    public static void createMenus() {
        optionsMenu = new OptionsMenu();
        hasMenus = true;
    }

    public static OptionsMenu getOptionsMenu() {
        if(!hasMenus ) {
            createMenus();
        }
        return optionsMenu;
    }
}
