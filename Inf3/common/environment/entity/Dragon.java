package environment.entity;

import util.SyncedMap;

final public class Dragon extends Entity {
	// db here
	public static SyncedMap<Dragon> instances = new SyncedMap<Dragon>();
	
	/**
	 * @return count of dragons in the db. -1 if the db is closed.
	 */
	public static int getDragonCount() {
		return instances.size();
	}
	
	public Dragon(int _x, int _y) {
		super(_x, _y, "Dragon");
		instances.put(id, this);
	}
	
	public Dragon(int _id, int _x, int _y, boolean _busy) {
		super(_id, _x, _y, "Dragon", _busy);
		instances.put(id, this);
	}
	
	@Override
	public void destruct() {
		super.destruct();
		instances.remove(id);
	}
}
