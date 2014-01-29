package environment.wrapper;

import java.util.Iterator;
import java.util.Random;

import listener.IDragonListener;
import listener.IListenable;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import tokenizer.DragonTokenizer;
import util.Configuration;
import util.Dice;
import util.SyncedMap;
import util.Vector2D;
import environment.MapCell;
import environment.Property;
import environment.entity.Dragon;

/**
 * Serversided wrapper for {@link Dragon}s.<br>
 * They are able to roam around on the map as long as they are not busy.
 * @author Daniel
 */
public class ServerDragon extends ServerEntity<Dragon> implements IListenable<IDragonListener>, IServerListener {
	public static SyncedMap<ServerDragon> instances = new SyncedMap<ServerDragon>();
	private long accu;
	private ListenerSet<IDragonListener> listeners;

	/**
	 * Constructor<br>
	 * Calls {@link #ServerDragon(Dragon, Server, boolean)} with TRUE for storing
	 * @param _wrappedObject the dragon that is wrapped in this wrapper
	 * @param _server reference to the currently running server
	 */
	public ServerDragon(Dragon _wrappedObject, Server _server) {
		this(_wrappedObject, _server, true);
	}
	
	/**
	 * Constructor<br>
	 * Creating a serverdragon causes it to register as listener for the server and registers the server as listener for itself.
	 * @param _wrappedObject wrapped dragon
	 * @param _server reference to the running server
	 * @param _storeInDb whether to store the wrapper or not
	 */
	public ServerDragon(Dragon _wrappedObject, Server _server, boolean _storeInDb) {
		super(_wrappedObject, _server, _storeInDb);
		tokenizer = new DragonTokenizer();
		listeners = new ListenerSet<IDragonListener>();
		instances.put(_wrappedObject.getId(), this);
		_server.getListeners().add(this);
		listeners.add(_server);
	}
	
	@Override
	public void destruct() {
		instances.remove(wrapped.getId());
		super.destruct();
	}

	@Override
	public void onTick(long _ms) {
		accu += _ms;
		if(!getWrappedObject().isBusy() && accu > Configuration.getInstance().getLong(Configuration.DRAGON_MOVE_INTERVAL)) {
			accu = 0;
			if(new Dice().roll(Configuration.getInstance().getInteger(Configuration.DRAGON_MOVE_CHANCE))) {
				int xdelta = 0,ydelta = 0;
				int dir = new Random().nextInt(4);
				switch(dir) {
				// up
					case 0: ydelta = -1; break;
				// down
					case 1:	ydelta = 1; break;
				// left
					case 2:	xdelta = -1; break;
				// right
					case 3:	xdelta = 1; break;
				}
				Vector2D pos = new Vector2D(getWrappedObject().getPosition());
				pos.x += xdelta;
				pos.y += ydelta;
				MapCell newPos = server.getMap().getWrappedObject().getCellAt(pos.x,pos.y);
				if(newPos != null && newPos.hasProperty(Property.WALKABLE)) {
					Vector2D oldPos = getWrappedObject().getPosition();
					getWrappedObject().setPosition(pos);
					Iterator<IDragonListener> it = listeners.iterator();
					while(it.hasNext()) {
						it.next().onMove(this, oldPos, pos);
					}
				}
			}
		}
	}

	@Override
	public void onShutdown(Server _server) {
		_server.getListeners().unregisterListener(this);
	}
	
	@Override
	public ListenerSet<IDragonListener> getListeners() {
		return listeners;
	}
}
