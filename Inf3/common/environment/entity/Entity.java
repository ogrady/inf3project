package environment.entity;

import java.util.Collection;

import util.SyncedMap;
import util.Vector2D;
import db.Idable;

/**
 * {@link Entity} that can stand around on the {@link Map}. Monsters or Players,
 * Items, whatever.
 * 
 * @author Daniel
 */
public class Entity implements Idable {
	// db here
	public static SyncedMap<Entity> instances = new SyncedMap<Entity>();
	private static Integer nextId = new Integer(0);
	protected Vector2D position;
	protected String desc;
	protected int id = -1;
	protected boolean busy;

	// protected Activator activator;

	/**
	 * Get a list of all {@link Entity}s
	 * 
	 * @return whole list of currently active {@link Entity}s
	 */
	public static Collection<Entity> getEntities() {
		// return Const.db.query(Entity.class);
		// db here
		return instances.values();
	}

	/**
	 * Get a {@link Entity} by it's unique ID
	 * 
	 * @param id
	 *            the unique ID
	 * @return the identified {@link Entity} or <code>null</code>
	 */
	public static Entity getEntity(final Integer _id) {
		// return new IdQuery<Entity>(Const.db).getById(_id);
		// db here
		return instances.get(_id);
	}

	/**
	 * Setter for ID
	 * 
	 * @param _id
	 *            new id
	 */
	@Override
	public void setId(final int _id) {
		instances.remove(id);
		id = _id;
		instances.put(id, this);
	}

	/**
	 * Unique ID for each {@link Entity}
	 * 
	 * @return unique ID
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * Checks the position of the {@link Entity}
	 * 
	 * @return the current position in the map array, represented by a 2D vector
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Set a whole new position
	 * 
	 * @param _pos
	 *            new position
	 */
	public void setPosition(final Vector2D _pos) {
		position = _pos;
	}

	/**
	 * @return whether the {@link Entity} is currently busy
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * Set the {@link Entity} busy or not
	 * 
	 * @param _isBusy
	 *            new value for busy
	 */
	public void setBusy(final boolean _isBusy) {
		busy = _isBusy;
	}

	/**
	 * Gets the descriptive String
	 * 
	 * @return a descriptive String, such as a name or an entity-description. If
	 *         the descriptive string is not set, the classname + the unique id
	 *         is sent
	 */
	public String getDescription() {
		return this.desc == null || this.desc.equals("") ? this.getClass()
				.getSimpleName() + id : desc;
	}

	/**
	 * Gives the {@link Entity} a new descriptive string
	 * 
	 * @param desc
	 *            new description
	 */
	public void setDescription(final String _desc) {
		desc = _desc;
	}

	/**
	 * Destructs an {@link Entity} and removes it from the list. It is then
	 * finalized.
	 */
	public void destruct() {
		Entity.instances.remove(id);
		try {
			finalize();
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor can be called from subclasses which then instantiate
	 * themselves via parsing
	 */
	protected Entity() {
	}

	/**
	 * Constructor
	 * 
	 * @param _x
	 *            initial x position in the map array
	 * @param _y
	 *            initial y position in the map array
	 * @param _desc
	 *            description string
	 */
	public Entity(final int _x, final int _y, final String _desc) {
		synchronized (nextId) {
			init(nextId++, _x, _y, _desc, false);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param _id
	 *            id for the {@link Entity}
	 * @param _x
	 *            initial x position in the map array
	 * @param _y
	 *            initial y position in the map array
	 * @param _desc
	 *            description string
	 * @param whether
	 *            the entity is currently busy
	 */
	public Entity(final int _id, final int _x, final int _y,
			final String _desc, final boolean _busy) {
		init(_id, _x, _y, _desc, _busy);
	}

	private void init(final int _id, final int _x, final int _y,
			final String _desc, final boolean _busy) {
		setId(_id);
		setPosition(new Vector2D(_x, _y));
		setDescription(_desc);
		setBusy(_busy);
		Entity.instances.put(_id, this);
	}

	@Override
	public String toString() {
		return String.format("%1$s at %2$s (%3$s)", this.desc, this.position,
				super.toString());
	}
}
