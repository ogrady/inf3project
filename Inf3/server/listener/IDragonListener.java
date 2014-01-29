package listener;

import util.Vector2D;
import environment.wrapper.ServerDragon;

public interface IDragonListener extends IListener {
	public void onMove(ServerDragon _dragon, Vector2D _oldpos, Vector2D _newpos);
}
