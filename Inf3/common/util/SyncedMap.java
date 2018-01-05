package util;

import java.util.concurrent.ConcurrentHashMap;

public class SyncedMap<T> extends ConcurrentHashMap<Integer, T> {
	private static final long serialVersionUID = 1L;
	private volatile int nextId = 0;

	public int add(T element) {
		this.put(nextId, element);
		int ret = nextId;
		nextId = nextId + 1 % Integer.MAX_VALUE;
		return ret;
	}
}
