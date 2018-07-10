package environment;

/**
 * {@link Property} of a {@link MapCell}, such as whether it is walkable or not
 * 
 * @author Daniel
 */
public enum Property {
	WALKABLE, WALL, FOREST, WATER, HUNTABLE;

	public static final String PROPERTY = "prop";

	public int getValue() {
		return (int) Math.pow(2, this.ordinal());
	}
}
