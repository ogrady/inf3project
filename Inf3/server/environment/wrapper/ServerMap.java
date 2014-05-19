package environment.wrapper;

import java.io.File;
import java.util.List;

import listener.IListenable;
import listener.IMapListener;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import tokenizer.ITokenizable;
import tokenizer.MapTokenizer;
import util.Configuration;
import util.Dice;
import environment.Map;
import environment.MapCell;
import environment.Property;
import environment.entity.Dragon;
import exception.MapException;

/**
 * server-sided wrapper for the {@link Map}.<br>
 * It is responsible for spawning dragons and takes care of cells that become
 * huntable over time.
 * 
 * @author Daniel
 */
public class ServerMap extends ServerWrapper<Map> implements IServerListener,
		IListenable<IMapListener>, ITokenizable {
	private long tickAccu;
	private final MapTokenizer tokenizer;
	private final ListenerSet<IMapListener> listeners = new ListenerSet<IMapListener>();
	private final String mapFile;

	/**
	 * @return the absolute filepath of the .bmp-file the wrapped map was
	 *         created from
	 */
	public String getMapFilePath() {
		return mapFile;
	}

	/**
	 * Constructor
	 * 
	 * @param _mapFile
	 *            .bmp-file to create the wrapped map from
	 * @param _server
	 *            reference to the running server
	 * @throws MapException
	 *             if the map could not be created (see {@link Map})
	 */
	public ServerMap(final File _mapFile, final Server _server)
			throws MapException {
		super(new Map(_mapFile), _server);
		tokenizer = new MapTokenizer();
		mapFile = _mapFile.getAbsolutePath();
	}

	/**
	 * Spawn a dragon on a random walkable cell
	 * 
	 * @return the cell the dragon was spawned on
	 */
	public MapCell spawnDragonOnRandomCell() {
		final MapCell mc = wrapped.getRandomWalkableCell();
		spawnDragon(mc);
		return mc;
	}

	/**
	 * Spawns a dragon on the given cell. This cell should be walkable (will not
	 * be checked).<br>
	 * Listeners will be notified of this event
	 * 
	 * @param _cell
	 *            cell to spawn the dragon on
	 */
	public void spawnDragon(final MapCell _cell) {
		final Dragon d = new Dragon(_cell.getX(), _cell.getY());
		for (final IMapListener mcl : this.listeners) {
			mcl.onSpawnDragon(_cell, d);
		}
	}

	/**
	 * If the accumulated milliseconds breach the threshold (update interval
	 * from the config) we will run through all cells in the map and make them
	 * huntable or spawn dragons on them at random (dependent on the chance,
	 * specified in the config)
	 */
	@Override
	public void onTick(final long _ms) {
		this.tickAccu += _ms;
		final Configuration conf = Configuration.getInstance();
		final long updateInterval = conf.getLong(Configuration.UPDATE_INTERVAL);
		if (this.tickAccu / updateInterval > 0) {
			final Dice dice = new Dice();
			final int maxHuntable = conf.getInteger(Configuration.MAX_HUNTABLE);
			final int maxDragon = conf.getInteger(Configuration.MAX_DRAGON);
			final int huntableChance = conf
					.getInteger(Configuration.HUNTABLE_CHANCE);
			final int dragonChance = conf
					.getInteger(Configuration.DRAGON_CHANCE);
			MapCell cell;
			tickAccu %= updateInterval;
			for (int i = 0; i < wrapped.getWidth(); i++) {
				for (int j = 0; j < wrapped.getHeight(); j++) {
					cell = wrapped.getCellAt(i, j);
					if (cell.hasProperty(Property.WALKABLE)) {
						if (wrapped.getHuntableCellCount() < maxHuntable
								&& cell.hasProperty(Property.FOREST)
								&& !cell.hasProperty(Property.HUNTABLE)
								&& dice.roll(huntableChance)) {
							setHuntableAt(i, j, true);
						}
						if (Dragon.getDragonCount() < maxDragon
								&& dice.roll(dragonChance)) {
							spawnDragon(cell);
						}
					}
				}
			}
		}
	}

	public void setHuntableAt(final int _x, final int _y,
			final boolean _huntable) {
		wrapped.setHuntableAt(_x, _y, _huntable);
		final MapCell cell = wrapped.getCellAt(_x, _y);
		for (final IMapListener mcl : this.listeners) {
			mcl.onToggleHuntable(cell, _huntable);
		}
	}

	@Override
	public ListenerSet<IMapListener> getListeners() {
		return listeners;
	}

	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(wrapped);
	}

	@Override
	public void onShutdown(final Server _server) {
		_server.getListeners().unregisterListener(this);
	}
}
