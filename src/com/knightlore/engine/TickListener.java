package com.knightlore.engine;

/**
 * This interface allows objects to use the game engine's ticker to receive
 * regular updates. Implementing classes must provide an interval, which
 * specifies how often they should be run, and an onTick() method specifying
 * what to do every interval.
 * 
 * @author Joe Ellis
 *
 */
public interface TickListener {

    /**
     * What to do.
     */
    public void onTick();

    /**
     * The interval between updates.
     * 
     * @return a long representing the interval between updates.
     */
    public long interval();

}
