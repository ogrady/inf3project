package listener;

import environment.wrapper.ServerDragon;
import util.Vector2D;

public interface IDragonListener extends IListener {
	default public void onMove(ServerDragon dragon, Vector2D oldpos, Vector2D newpos) {
	};
}
