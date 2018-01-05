package environment.entity;

import util.SyncedMap;

final public class Dragon extends Entity {
	// db here
	public static SyncedMap<Dragon> instances = new SyncedMap<>();

	/**
	 * @return count of dragons in the db. -1 if the db is closed.
	 */
	public static int getDragonCount() {
		return instances.size();
	}

	public Dragon(int x, int y) {
		super(x, y, "Dragon");
		instances.put(_id, this);
	}

	public Dragon(int id, int x, int y, boolean busy) {
		super(id, x, y, "Dragon", busy);
		instances.put(id, this);
	}

	@Override
	public void destruct() {
		super.destruct();
		instances.remove(_id);
	}
}
