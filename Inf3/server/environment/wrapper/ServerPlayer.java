package environment.wrapper;

import java.util.Iterator;

import listener.IListenable;
import listener.IPlayerListener;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import tokenizer.PlayerTokenizer;
import util.ServerConst;
import util.SyncedMap;
import arena.Arena;
import environment.entity.Player;

/**
 * server-sided wrapper for players. 
 * @author Daniel
 *
 */
public class ServerPlayer extends ServerEntity<Player> implements IListenable<IPlayerListener>, IServerListener {
	public boolean authenticated;
	public static SyncedMap<ServerPlayer> instances = new SyncedMap<ServerPlayer>();
	private ListenerSet<IPlayerListener> listeners = new ListenerSet<IPlayerListener>();
	
	/**
	 * Players can challenge each other to minigames. Whenever player A challenges player B
	 * He enters a new {@link Arena} and waits. When B accepts the challenge
	 * we can check whether he was actually challenged (= he is player2 in the {@link Arena})
	 */
	private Arena<?> arena;
	public Arena<?> getArena() { return arena; }
	public void setArena(Arena<?> _arena) { arena = _arena; }
	
	/**
	 * Adds a certain amount of points (as proxy for {@link Player#addPoints(int)}.<br>
	 * Adding points for an undestructed player will broadcast the player
	 * @param _amount
	 */
	public void addPoints(int _amount) {
		wrapped.addPoints(_amount);
		if(!destructed && _amount != 0) {
			server.broadcast(this.getWrappedObject(), ServerConst.UPD);
		}
	}
	
	/**
	 * Constructor<br>
	 * Calls {@link #ServerPlayer(Player, Server, boolean)} with TRUE as _storeInDb
	 * @param _wrappedObject the {@link Player} this instance wraps
	 * @param _server reference to the running server
	 */
	public ServerPlayer(Player _wrappedObject, Server _server) {
		this(_wrappedObject, _server, true);
	}
	
	/**
	 * Constructor
	 * @param _wrappedObject the {@link Player} this instance wraps
	 * @param _server reference to the running server
	 * @param _store whether or not to store the instance
	 */
	public ServerPlayer(Player _wrappedObject, Server _server, boolean _store) {
		super(_wrappedObject, _server, _store);
		tokenizer = new PlayerTokenizer();
		_server.getListeners().add(this);
		if(_store) {
			instances.put(_wrappedObject.getId(), this);
		}
	}
	
	@Override
	public void destruct() {
		instances.remove(wrapped.getId());
		super.destruct();
		Iterator<IPlayerListener> it = listeners.iterator();
		while(it.hasNext()) {
			it.next().onDisconnect(this);
		}
	}

	@Override
	public void onTick(long ms) {}

	@Override
	public void onShutdown(Server _server) {
		_server.getListeners().unregisterListener(this);
		destruct();
	}
	
	@Override
	public ListenerSet<IPlayerListener> getListeners() {
		return listeners;
	}
}
