package listener;

import environment.MapCell;
import environment.entity.Dragon;

public interface IMapListener extends IListener {
	public void onToggleHuntable(MapCell _cell, boolean _huntable);
	public void onSpawnDragon(MapCell _cell, Dragon _dragon);
}
