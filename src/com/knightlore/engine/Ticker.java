package com.knightlore.engine;

public class Ticker {
	
	private static long tick;
	private static final long TICK_MAX = 10000 * 60;
	
	public static long getTime() {
		return tick;
	}
	
	protected static void tick() {
		tick = (tick + 1) % TICK_MAX;
	}

}
