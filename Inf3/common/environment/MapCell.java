package environment;

import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import util.Bitmask;
import environment.entity.Entity;

/**
 * {@link MapCell} of which a {@link ServerMap} consists.
 * 
 * @author Daniel
 */
public class MapCell {
	@JsonIgnore
	protected Vector<Entity> _entities;
	protected Vector<Property> _properties;
	@JsonIgnore
	protected Bitmask _propertyMap;
	@JsonIgnore
	protected long _tickAccu;
	@JsonIgnore
	protected Map _map;
	protected int _x;
	protected int _y;

	public int getX() {
		return this._x;
	}

	public int getY() {
		return this._y;
	}

	@JsonIgnore
	public void setMap(Map m) {
		this._map = m;
	}

	/**
	 * List {@link Entity}s
	 * 
	 * @return a list of {@link Entity}s
	 */
	@JsonIgnore
	public Vector<Entity> getEntities() {
		return this._entities;
	}

	/**
	 * The list of properties this {@link MapCell} holds
	 * 
	 * @return
	 */
	public Vector<Property> getProperties() {
		return _properties;
	}

	/**
	 * Collects the direct neighbours of the cell
	 * 
	 * @return array of {@link MapCell}s that are directly adjacent to the current
	 *         cell (4 elements in total, vertically and horizontally) That is:<br>
	 *         0: left<br>
	 *         1: top<br>
	 *         2: right<br>
	 *         3: down<br>
	 *         Be aware that if a cell does not have a neighbour to any side the
	 *         value at this position is NULL! For example: if a cell does not have
	 *         a top neighbour, getNeighbours()[1] will be NULL. So additional
	 *         checking is needed.
	 */
	@JsonIgnore
	public MapCell[] getNeighbours() {
		MapCell[] n = new MapCell[4];
		if (_map != null) {
			n[0] = _map.getCellAt(this._x - 1, this._y);
			n[1] = _map.getCellAt(this._x, this._y - 1);
			n[2] = _map.getCellAt(this._x + 1, this._y);
			n[3] = _map.getCellAt(this._x, this._y + 1);
		}
		return n;
	}

	protected MapCell() {
		this._entities = new Vector<Entity>();
		this._properties = new Vector<Property>();
		this._propertyMap = new Bitmask();
	}

	/**
	 * Constructor
	 * 
	 * @param x
	 *            the x-coordinate of the {@link MapCell} in the {@link Map}
	 * @param y
	 *            the y-coordinate of the {@link MapCell} in the {@link Map}
	 */
	public MapCell(Map map, int x, int y) {
		this();
		this._map = map;
		this._x = x;
		this._y = y;
	}

	/**
	 * Add an {@link Entity} to the {@link MapCell}
	 * 
	 * @param e
	 *            {@link Entity} to add
	 */
	public void addEntity(Entity e) {
		this._entities.add(e);
	}

	/**
	 * Remove an {@link Entity} from the {@link MapCell}
	 * 
	 * @param e
	 *            {@link Entity} to add
	 */
	public void removeEntity(Entity e) {
		this._entities.remove(e);
	}

	/**
	 * Add a {@link Property} to the {@link MapCell} (no duplicates possible)
	 * 
	 * @param p
	 *            the {@link Property} to add
	 * @return true, if the value was added, false if not (value was already in
	 *         list)
	 */
	public boolean addProperty(Property p) {
		boolean added = false;
		if (p != null && !hasProperty(p)) {
			added = _properties.add(p);
			if (added) {
				_propertyMap.add(p.getValue());
			}
		}
		return added;
	}

	/**
	 * Add a whole list of {@link Property}s to the cell
	 * 
	 * @param p
	 *            new {@link Property}s
	 */
	public void addProperties(Property[] p) {
		if (p != null) {
			for (Property prop : p) {
				addProperty(prop);
			}
		}
	}

	/**
	 * Remove a {@link Property} from the {@link MapCell}
	 * 
	 * @param p
	 *            the {@link Property} to remove
	 * @return true, if the value was removed, false, if it wasn't in the list
	 */
	public boolean removeProperty(Property p) {
		boolean removed = _properties.remove(p);
		if (removed) {
			_propertyMap.remove(p.getValue());
		}
		return removed;
	}

	/**
	 * Checks whether the {@link MapCell} has a given {@link Property}
	 * 
	 * @param p
	 *            the {@link Property} to check for
	 * @return true, if it is contained in the list of {@link Property}s
	 */
	public boolean hasProperty(Property p) {
		return _propertyMap.has(p.getValue());
	}

	/**
	 * Two {@link MapCell}s are the same if they have the same coordinate in a
	 * {@link Map}
	 * 
	 * @param other
	 *            other {@link MapCell}
	 * @return true, if the cells share the same coords
	 */
	public boolean equals(MapCell other) {
		return this._x == other._x && this._y == other._y;
	}

	@Override
	public String toString() {
		return String.format("MapCell(%d|%d)", this._x, this._y);
	}
}
