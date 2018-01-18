package com.knightlore.game;

import java.util.Random;

import com.knightlore.render.environment.DarkOutdoorsEnvironment;
import com.knightlore.render.environment.IEnvironment;

public class Map {

	/*
	 * TODO: read in maps from files/procedurally generate.
	 */

	private int width, height;
	public int[][] map;

	/**
	 * Maps have associated environments.
	 */
	private IEnvironment environment;

	public int[][] getMapArray() {
		return map;
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public Map(int w, int h) {
		width = w;
		height = h;
		map = new int[w][h];
		environment = IEnvironment.DARK_OUTDOORS;
	}

	public void addWalls() {
		for (int i = 0; i < width; i++) {
			map[i][0] = 1;
			map[i][height - 1] = 1;
		}
		for (int j = 0; j < height; j++) {
			map[0][j] = 1;
			map[width - 1][j] = 1;
		}
	}

	public static Map randMap() {
		Random rand = new Random();
		int w = 10 + rand.nextInt(20);
		int h = 10 + rand.nextInt(5);
		Map m = new Map(w, h);

		// make everything a wall
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				m.map[i][j] = 1;
			}
		}

		int r = 0;
		// make horizontal rows of free space
		for (int i = 0; i < w; i++) {
			r = rand.nextInt(100);
			if (r > 80) {
				for (int j = 0; j < h; j++) {
					m.map[i][j] = 0;
				}
			} else if (r > 40) {
				for (int j = 0; j < (h / 2); j++) {
					m.map[i][j] = 0;
				}
			}
		}

		// make vertical rows of free space
		for (int j = 0; j < h; j++) {
			r = rand.nextInt(100);
			if (rand.nextInt(100) > 50) {
				for (int i = 0; i < w; i++) {
					m.map[i][j] = 0;
				}
			} else if (r > 20) {
				for (int i = 0; i < (w / 2); i++) {
					m.map[i][j] = 0;
				}
			}
		}

		m.makeSymX();
		m.makeSymY();
		m.addWalls();
		return m;
	}

	// reflection in x-axis
	private void makeSymX() {
		int[][] symMap = new int[width][height * 2];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				symMap[i][j] = map[i][j];
				symMap[i][(height * 2 - 1) - j] = map[i][j];
			}
		}
		height = height * 2;
		map = symMap;
	}

	private void makeSymY() {
		int[][] symMap = new int[width * 2][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				symMap[i][j] = map[i][j];
				symMap[(width * 2 - 1) - i][j] = map[i][j];
			}
		}
		width = width * 2;
		map = symMap;
	}

	private static Map joinUpR(Map m1, Map m2) {

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

	}

	public String toString() {

		String s = "MAP\n" + "WIDTH = " + width + "\n" + "HEIGHT = " + height + "\n";
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (map[i][j] == 0)
					s = s += " ";
				else if (map[i][j] == 3)
					s = s + "Y";
				else
					s = s + "X";
			}
			s = s + "\n";
		}

		return s;
	}

	// dumb test
//	public static void main(String args[]) {
//		Map m = randMap();
//		// m = joinUpR(m,m);
//		System.out.println(m.toString());
//	}

}
