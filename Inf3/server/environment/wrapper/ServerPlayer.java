package environment.wrapper;

import java.util.Iterator;

import arena.Arena;
import environment.entity.Player;
import listener.IListenable;
import listener.IPlayerListener;
import listener.IServerListener;
import listener.ListenerSet;
import server.Server;
import util.ServerConst;
import util.SyncedMap;

/**
 * server-sided wrapper for players.
 * 
 * @author Daniel
 *
 */
public class ServerPlayer extends ServerEntity<Player> implements IListenable<IPlayerListener>, IServerListener {
	public static SyncedMap<ServerPlayer> instances = new SyncedMap<>();
	public boolean _authenticated;
	private final ListenerSet<IPlayerListener> _listeners = new ListenerSet<>();

	/**
	 * Players can challenge each other to minigames. Whenever player A challenges
	 * player B He enters a new {@link Arena} and waits. When B accepts the
	 * challenge we can check whether he was actually challenged (= he is player2 in
	 * the {@link Arena})
	 */
	private Arena<?> _arena;

	public Arena<?> getArena() {
		return _arena;
	}

	public void setArena(Arena<?> arena) {
		_arena = arena;
	}

	/**
	 * Adds a certain amount of points (as proxy for
	 * {@link Player#addPoints(int)}.<br>
	 * Adding points for an undestructed player will broadcast the player
	 * 
	 * @param amount
	 */
	public void addPoints(int amount) {
		_wrapped.addPoints(amount);
		if (!destructed && amount != 0) {
			_server.broadcast(this.getWrappedObject(), ServerConst.UPD);
		}
	}

	/**
	 * Constructor<br>
	 * Calls {@link #ServerPlayer(Player, Server, boolean)} with TRUE as _storeInDb
	 * 
	 * @param wrappedObject
	 *            the {@link Player} this instance wraps
	 * @param server
	 *            reference to the running server
	 */
	public ServerPlayer(Player wrappedObject, Server server) {
		this(wrappedObject, server, true);
	}

	/**
	 * Constructor
	 * 
	 * @param wrappedObject
	 *            the {@link Player} this instance wraps
	 * @param server
	 *            reference to the running server
	 * @param store
	 *            whether or not to store the instance
	 */
	public ServerPlayer(Player wrappedObject, Server server, boolean store) {
		super(wrappedObject, server, store);
		server.getListeners().add(this);
		if (store) {
			instances.put(wrappedObject.getId(), this);
		}
	}

	@Override
	public void destruct() {
		instances.remove(_wrapped.getId());
		super.destruct();
		final Iterator<IPlayerListener> it = _listeners.iterator();
		while (it.hasNext()) {
			it.next().onDisconnect(this);
		}
	}

	@Override
	public void onTick(long ms) {
	}

	@Override
	public void onShutdown(Server server) {
		server.getListeners().unregisterListener(this);
		destruct();
	}

	@Override
	public ListenerSet<IPlayerListener> getListeners() {
		return _listeners;
	}
}
