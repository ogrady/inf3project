package environment.entity;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import db.Idable;
import util.SyncedMap;
import util.Vector2D;

/**
 * {@link Entity} that can stand around on the {@link Map}. Monsters or Players,
 * Items, whatever.
 * 
 * @author Daniel
 */

// @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include =
// JsonTypeInfo.As.PROPERTY, property = "type")
// @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "exercise_type", visible = true)
public class Entity implements Idable {
	// db here
	public static SyncedMap<Entity> instances = new SyncedMap<>();
	private static Integer nextId = new Integer(0);
	protected Vector2D _position;
	protected String _desc;
	protected int _id = -1;
	protected boolean _busy;

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
	public static Entity getEntity(final Integer id) {
		// return new IdQuery<Entity>(Const.db).getById(_id);
		// db here
		return instances.get(id);
	}
	
	/**
	 * @return the type of this entity, which is just the most concrete classname. This is required to include the entity type in JSON
	 */
	public String getType() {
		return getClass().getSimpleName();
	}

	/**
	 * Setter for ID
	 * 
	 * @param _id
	 *            new id
	 */
	@Override
	public void setId(final int id) {
		instances.remove(_id);
		_id = id;
		instances.put(_id, this);
	}

	/**
	 * Unique ID for each {@link Entity}
	 * 
	 * @return unique ID
	 */
	@Override
	public int getId() {
		return _id;
	}

	/**
	 * Checks the position of the {@link Entity}
	 * 
	 * @return the current position in the map array, represented by a 2D vector
	 */
	public Vector2D getPosition() {
		return _position;
	}

	/**
	 * Set a whole new position
	 * 
	 * @param pos
	 *            new position
	 */
	public void setPosition(final Vector2D pos) {
		_position = pos;
	}

	/**
	 * @return whether the {@link Entity} is currently busy
	 */
	public boolean isBusy() {
		return _busy;
	}

	/**
	 * Set the {@link Entity} busy or not
	 * 
	 * @param isBusy
	 *            new value for busy
	 */
	public void setBusy(final boolean isBusy) {
		_busy = isBusy;
	}

	/**
	 * Gets the descriptive String
	 * 
	 * @return a descriptive String, such as a name or an entity-description. If the
	 *         descriptive string is not set, the classname + the unique id is sent
	 */
	public String getDescription() {
		return this._desc == null || this._desc.equals("") ? this.getClass().getSimpleName() + _id : _desc;
	}

	/**
	 * Gives the {@link Entity} a new descriptive string
	 * 
	 * @param _desc
	 *            new description
	 */
	public void setDescription(final String desc) {
		_desc = desc;
	}

	/**
	 * Destructs an {@link Entity} and removes it from the list. It is then
	 * finalized.
	 */
	public void destruct() {
		Entity.instances.remove(_id);
		try {
			finalize();
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor can be called from subclasses which then instantiate themselves
	 * via parsing
	 */
	protected Entity() {
	}

	/**
	 * Constructor
	 * 
	 * @param x
	 *            initial x position in the map array
	 * @param y
	 *            initial y position in the map array
	 * @param desc
	 *            description string
	 */
	public Entity(final int x, final int y, final String desc) {
		synchronized (nextId) {
			init(nextId++, x, y, desc, false);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            id for the {@link Entity}
	 * @param x
	 *            initial x position in the map array
	 * @param y
	 *            initial y position in the map array
	 * @param desc
	 *            description string
	 * @param whether
	 *            the entity is currently busy
	 */
	public Entity(final int id, final int x, final int y, final String desc, final boolean busy) {
		init(id, x, y, desc, busy);
	}

	private void init(final int id, final int x, final int y, final String desc, final boolean busy) {
		setId(id);
		setPosition(new Vector2D(x, y));
		setDescription(desc);
		setBusy(busy);
		Entity.instances.put(id, this);
	}

	@Override
	public String toString() {
		return String.format("%1$s at %2$s (%3$s)", this._desc, this._position, super.toString());
	}
}
