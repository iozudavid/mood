package com.knightlore.utils;

import com.knightlore.game.Team;

public final class Utils {
    
    /**
     * To be used for getting minimap color
     * @param t - the team for the color
     * @returns red or blue, or white if no team
     */
    public static int colorForTeam(Team t) {
        if(t == Team.blue) {
            return 0xFF00FF88;
        }
        if(t == Team.red) {
            return 0xFFFF0000;
        }
        return 0xFFFFFFFF;
    }
}
