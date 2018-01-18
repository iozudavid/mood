package com.knightlore.render.environment;

import com.knightlore.render.Screen;

public interface IEnvironment {

	public static final IEnvironment DARK_OUTDOORS = new DarkOutdoorsEnvironment();
	public static final IEnvironment LIGHT_OUTDOORS = new LightOutdoorsEnvironment();

	public static final IEnvironment[] ENVIRONMENTS = new IEnvironment[] { DARK_OUTDOORS, LIGHT_OUTDOORS };

	public void renderEnvironment(Screen screen);

	public int getDarkness();

}
