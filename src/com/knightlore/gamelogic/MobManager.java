package com.knightlore.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.knightlore.engine.GameObject;
import com.knightlore.game.entity.Mob;

public class MobManager extends GameObject {

	private List<Mob> mobs;

	public MobManager() {
		mobs = new ArrayList<Mob>();
	}

	public void addMob(Mob m) {
		mobs.add(m);
	}

	public void delMob(Mob m) {
		mobs.remove(m);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onUpdate() {
		ListIterator<Mob> itr = mobs.listIterator();
		while (itr.hasNext()) {
			Mob m = itr.next();
			if (!m.exists())
				itr.remove();
		}
	}

	@Override
	public void onDestroy() {

	}

}
