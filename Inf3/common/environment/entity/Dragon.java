package environment.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import util.SyncedMap;

final public class Dragon extends Entity {
	// db here
	public static SyncedMap<Dragon> instances = new SyncedMap<>();

	public static final List<String> dragonNames = Arrays.asList("Elliot", "Smaug", "Zhaitan", "Modremoth", "Primordus",
			"Kralkatorrik", "Jormag", "Fafnir", "Eborsisk", "Jabberwocky", "Quetzalcoatl", "Yowler", "Dagahra",
			"Bahamut", "Glint", "Gorbash", "Scales", "Granymyr", "Tiamat", "Envy", "Nidhogg", "Elvarg");

	public static String getRandomDragonName() {
		return dragonNames.get(new Random().nextInt(dragonNames.size()));
	}

	/**
	 * @return count of dragons in the db. -1 if the db is closed.
	 */
	public static int getDragonCount() {
		return instances.size();
	}

	public Dragon(int x, int y) {
		super(x, y, getRandomDragonName());
		instances.put(_id, this);
	}

	/*
	 * public Dragon(int id, int x, int y, boolean busy) { super(id, x, y, "Dragon",
	 * busy); instances.put(id, this); }
	 */

	@Override
	public void destruct() {
		super.destruct();
		instances.remove(_id);
	}
}
