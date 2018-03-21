package com.knightlore.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Timekeeper for the game engine. Keeps track of the current tick value and
 * allows other objects to be registered as TickListeners so that they can
 * receive updates on a fixed schedule.
 * 
 * @author Joe Ellis
 *
 */
public class Ticker {

    /**
     * The current tick value. One tick corresponds to a single game update.
     */
    private long tick;

    /**
     * Somewhat left in as a joke because this will never happen. Assuming that
     * we have 60 updates per second, it'll take 4,874,520,144 years for this to
     * ever overflow.
     */
    private final long TICK_MAX = Long.MAX_VALUE - 1337;

    /**
     * Listeners attached to this ticker.
     */
    private List<TickListener> tickListeners;

    protected Ticker() {
        tick = 0;
        tickListeners = new ArrayList<>();
    }

    /**
     * Register a tick listener to receive regular updates.
     * 
     * @param t
     *            the tick listener to receive updates.
     */
    public void addTickListener(TickListener t) {
        tickListeners.add(t);
    }

    /**
     * Gets the current time in ticks.
     * 
     * @return the current time in ticks.
     */
    public long getTime() {
        return tick;
    }

    /**
     * Tick! This is called from the game engine. On each tick, the tick counter
     * is incremented. All tick listeners are run if it is their time.
     */
    protected void tick() {
        tick++;
        ListIterator<TickListener> itr = tickListeners.listIterator();
        while (itr.hasNext()) {
            TickListener t = itr.next();
            // if there's not an interval, ignore it.
            if (t.interval() == 0) {
                continue;
            }

            if (tick % t.interval() == 0) {
                t.onTick();
            }
        }
    }

}
