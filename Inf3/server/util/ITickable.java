package util;

/**
 * Objects that can receive updates
 * 
 * @author Daniel
 */
public interface ITickable {
	/**
	 * Called by the server when a tick has passed
	 * 
	 * @param ms
	 *            passed milliseconds since the last tick
	 */
	public void tick(long ms);
}
