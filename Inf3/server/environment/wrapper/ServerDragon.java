package environment.wrapper;

import java.util.Iterator;
import java.util.Random;

import environment.MapCell;
import environment.Property;
import environment.entity.Dragon;
import listener.IDragonListener;
import listener.IListenable;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import util.Configuration;
import util.Dice;
import util.SyncedMap;
import util.Vector2D;

/**
 * Server sided wrapper for {@link Dragon}s.<br>
 * They are able to roam around on the map as long as they are not busy.
 * 
 * @author Daniel
 */
public class ServerDragon extends ServerEntity<Dragon> implements IListenable<IDragonListener>, IServerListener {
	public static SyncedMap<ServerDragon> instances = new SyncedMap<>();
	private long _accu;
	private final ListenerSet<IDragonListener> _listeners;

	/**
	 * Constructor<br>
	 * Calls {@link #ServerDragon(Dragon, Server, boolean)} with TRUE for storing
	 * 
	 * @param wrappedObject
	 *            the dragon that is wrapped in this wrapper
	 * @param server
	 *            reference to the currently running server
	 */
	public ServerDragon(Dragon wrappedObject, Server server) {
		this(wrappedObject, server, true);
	}

	/**
	 * Constructor<br>
	 * Creating a {@link ServerDragon} causes it to register as listener for the
	 * server and registers the server as listener for itself.
	 * 
	 * @param wrappedObject
	 *            wrapped dragon
	 * @param server
	 *            reference to the running server
	 * @param storeInDb
	 *            whether to store the wrapper or not
	 */
	public ServerDragon(Dragon wrappedObject, Server server, boolean storeInDb) {
		super(wrappedObject, server, storeInDb);
		_listeners = new ListenerSet<>();
		instances.put(wrappedObject.getId(), this);
		server.getListeners().add(this);
		_listeners.add(server);
	}

	@Override
	public void destruct() {
		instances.remove(_wrapped.getId());
		super.destruct();
	}

	@Override
	public void onTick(long ms) {
		_accu += ms;
		if (!getWrappedObject().isBusy()
				&& _accu > Configuration.getInstance().getLong(Configuration.DRAGON_MOVE_INTERVAL)) {
			_accu = 0;
			if (new Dice().roll(Configuration.getInstance().getInteger(Configuration.DRAGON_MOVE_CHANCE))) {
				int xdelta = 0, ydelta = 0;
				final int dir = new Random().nextInt(4);
				switch (dir) {
				// up
				case 0:
					ydelta = -1;
					break;
				// down
				case 1:
					ydelta = 1;
					break;
				// left
				case 2:
					xdelta = -1;
					break;
				// right
				case 3:
					xdelta = 1;
					break;
				}
				final Vector2D pos = new Vector2D(getWrappedObject().getPosition());
				pos.x += xdelta;
				pos.y += ydelta;
				final MapCell newPos = _server.getMap().getWrappedObject().getCellAt(pos.x, pos.y);
				if (newPos != null && newPos.hasProperty(Property.WALKABLE)) {
					final Vector2D oldPos = getWrappedObject().getPosition();
					getWrappedObject().setPosition(pos);
					final Iterator<IDragonListener> it = _listeners.iterator();
					while (it.hasNext()) {
						it.next().onMove(this, oldPos, pos);
					}
				}
			}
		}
	}

	@Override
	public void onShutdown(Server server) {
		server.getListeners().unregisterListener(this);
	}

	@Override
	public ListenerSet<IDragonListener> getListeners() {
		return _listeners;
	}
}
