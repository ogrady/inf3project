package environment.wrapper;

import java.io.File;
import java.util.List;

import environment.Map;
import environment.MapCell;
import environment.Property;
import environment.entity.Dragon;
import exception.MapException;
import listener.IListenable;
import listener.IMapListener;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import tokenizer.ITokenizable;
import tokenizer.MapTokenizer;
import util.Configuration;
import util.Dice;

/**
 * server-sided wrapper for the {@link Map}.<br>
 * It is responsible for spawning dragons and takes care of cells that become
 * huntable over time.
 * 
 * @author Daniel
 */
public class ServerMap extends ServerWrapper<Map> implements IServerListener, IListenable<IMapListener>, ITokenizable {
	private long _tickAccu;
	@Deprecated
	private final MapTokenizer _tokenizer;
	private final ListenerSet<IMapListener> _listeners = new ListenerSet<>();
	private final String _mapFile;

	/**
	 * @return the absolute filepath of the .bmp-file the wrapped map was created
	 *         from
	 */
	public String getMapFilePath() {
		return _mapFile;
	}

	/**
	 * Constructor
	 * 
	 * @param _mapFile
	 *            .bmp-file to create the wrapped map from
	 * @param server
	 *            reference to the running server
	 * @throws MapException
	 *             if the map could not be created (see {@link Map})
	 */
	public ServerMap(final File mapFile, final Server server) throws MapException {
		super(new Map(mapFile), server);
		_tokenizer = new MapTokenizer();
		_mapFile = mapFile.getAbsolutePath();
	}

	/**
	 * Spawn a dragon on a random walkable cell
	 * 
	 * @return the cell the dragon was spawned on
	 */
	public MapCell spawnDragonOnRandomCell() {
		final MapCell mc = _wrapped.getRandomWalkableCell();
		spawnDragon(mc);
		return mc;
	}

	/**
	 * Spawns a dragon on the given cell. This cell should be walkable (will not be
	 * checked).<br>
	 * Listeners will be notified of this event
	 * 
	 * @param cell
	 *            cell to spawn the dragon on
	 */
	public void spawnDragon(final MapCell cell) {
		final Dragon d = new Dragon(cell.getX(), cell.getY());
		for (final IMapListener mcl : this._listeners) {
			mcl.onSpawnDragon(cell, d);
		}
	}

	/**
	 * If the accumulated milliseconds breach the threshold (update interval from
	 * the config) we will run through all cells in the map and make them huntable
	 * or spawn dragons on them at random (dependent on the chance, specified in the
	 * config)
	 */
	@Override
	public void onTick(final long ms) {
		this._tickAccu += ms;
		final Configuration conf = Configuration.getInstance();
		final long updateInterval = conf.getLong(Configuration.UPDATE_INTERVAL);
		if (this._tickAccu / updateInterval > 0) {
			final Dice dice = new Dice();
			final int maxHuntable = conf.getInteger(Configuration.MAX_HUNTABLE);
			final int maxDragon = conf.getInteger(Configuration.MAX_DRAGON);
			final int huntableChance = conf.getInteger(Configuration.HUNTABLE_CHANCE);
			final int dragonChance = conf.getInteger(Configuration.DRAGON_CHANCE);
			MapCell cell;
			_tickAccu %= updateInterval;
			for (int i = 0; i < _wrapped.getWidth(); i++) {
				for (int j = 0; j < _wrapped.getHeight(); j++) {
					cell = _wrapped.getCellAt(i, j);
					if (cell.hasProperty(Property.WALKABLE)) {
						if (_wrapped.getHuntableCellCount() < maxHuntable && cell.hasProperty(Property.FOREST)
								&& !cell.hasProperty(Property.HUNTABLE) && dice.roll(huntableChance)) {
							setHuntableAt(i, j, true);
						}
						if (Dragon.getDragonCount() < maxDragon && dice.roll(dragonChance)) {
							spawnDragon(cell);
						}
					}
				}
			}
		}
	}

	public void setHuntableAt(final int x, final int y, final boolean huntable) {
		_wrapped.setHuntableAt(x, y, huntable);
		final MapCell cell = _wrapped.getCellAt(x, y);
		for (final IMapListener mcl : this._listeners) {
			mcl.onToggleHuntable(cell, huntable);
		}
	}

	@Override
	public ListenerSet<IMapListener> getListeners() {
		return _listeners;
	}

	@Override
	public List<String> tokenize() {
		return _tokenizer.tokenize(_wrapped);
	}

	@Override
	public void onShutdown(final Server _server) {
		_server.getListeners().unregisterListener(this);
	}
}
