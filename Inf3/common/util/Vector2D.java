package util;


/**
 * 2D vector
 * @author Daniel
 */
public class Vector2D {
	public int x,y;

	/**
	 * Constructor
	 * @param _x initial x
	 * @param _y initial y
	 */
	public Vector2D(int _x, int _y) {
		x = _x;
		y = _y;
	}
	
	/**
	 * Create a vector from another (cloning)
	 * @param _other
	 */
	public Vector2D(Vector2D _other) {
		x = _other.x;
		y = _other.y;
	}
	
	/**
	 * Equal when positions are the same
	 * @param _other
	 * @return
	 */
	public boolean equals(Vector2D _other) {
		return x == _other.x && y == _other.y;
	}
	
	public String toString() {
		return String.format("(%1$d|%2$d)", x, y);
	}
}
