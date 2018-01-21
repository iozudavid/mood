package com.knightlore.game.area;
import com.knightlore.game.tile.*;
import com.knightlore.render.environment.IEnvironment;

public class Map extends Area {
    // TODO: read in maps from files/procedurally generate.
    // Maps have associated environments
	private final IEnvironment environment;

	public IEnvironment getEnvironment() {
		return environment;
	}

	Map(Tile[][] grid, IEnvironment environment) {
		super(grid);
		this.environment = environment;
	}

	/* private static Map joinUpR(Map m1, Map m2) { TODO not used, dead code? if so, delete

		// ensure same height
		assert (m1.height == m2.height);

		Map m3 = new Map(m1.width + m2.width, m1.height);

		for (int i = 0; i < m1.height; i++) {
			for (int j = 0; j < m1.width; j++) {
				m3.map[i][j] = m1.map[i][j];
			}
			for (int k = 0; k < m2.width; k++) {
				m3.map[i][m1.width + 1 + k] = m2.map[i][k];
			}
		}

		return m3;

	} */

	// dumb test
	public static void main(String args[]) {
		Map m = AreaFactory.createRandomMap(IEnvironment.LIGHT_OUTDOORS);
		// // m = joinUpR(m,m);
		System.out.println(m.toString());
	}

}
