package db;

/**
 * Anything that can have a unique id.
 * Used for db-queries
 * @author Daniel
 */
public interface Idable {
	int getId();
	void setId(int _id);
}
