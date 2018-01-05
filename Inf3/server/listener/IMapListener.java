package listener;

import environment.MapCell;
import environment.entity.Dragon;

public interface IMapListener extends IListener {
	default public void onToggleHuntable(MapCell cell, boolean huntable) {
	};

	default public void onSpawnDragon(MapCell cell, Dragon dragon) {
	};
}
