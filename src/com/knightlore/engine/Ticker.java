package com.knightlore.engine;

import java.util.ArrayList;
import java.util.List;

public class Ticker {

	private long tick;
	private final long TICK_MAX = 10000 * 60;

	private List<TickListener> tickListeners;

	protected Ticker() {
		tick = 0;
		tickListeners = new ArrayList<TickListener>();
	}

	public void addTickListener(TickListener t) {
		tickListeners.add(t);
	}

	public long getTime() {
		return tick;
	}

	protected void tick() {
		tick = (tick + 1) % TICK_MAX;
		for (TickListener t : tickListeners) {
			if (tick % t.interval() == 0) {
				t.onTick();
			}
		}
	}

}
