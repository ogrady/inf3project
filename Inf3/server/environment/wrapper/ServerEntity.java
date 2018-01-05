package environment.wrapper;

import java.util.List;

import environment.entity.Entity;
import environment.entity.Player;
import server.Server;
import tokenizer.ITokenizable;
import tokenizer.Tokenizer;
import util.ServerConst;
import util.SyncedMap;

abstract public class ServerEntity<E extends Entity> extends ServerWrapper<E> implements ITokenizable {
	// db here
	public static SyncedMap<ServerEntity<?>> instances = new SyncedMap<>();
	protected Tokenizer<E> tokenizer;
	public boolean destructed;

	public ServerEntity(E wrappedObject, Server server, boolean store) {
		super(wrappedObject, server);
		preDBHook();
		if (store) {
			instances.put(wrappedObject.getId(), this);
		}
	}

	/**
	 * Proxy to set the busy attribute of the wrapped {@link Player}. If calling the
	 * setter changed the attribute it will broadcast itself as long it is not
	 * disconnected.
	 * 
	 * @param busy
	 */
	public void setBusy(boolean busy) {
		final boolean former = _wrapped.isBusy();
		_wrapped.setBusy(busy);
		if (!destructed && former != busy) {
			_server.broadcast(this.getWrappedObject(), ServerConst.UPD);
		}
	}

	public void destruct() {
		destructed = true;
		instances.remove(_wrapped.getId());
		_wrapped.destruct();
	}

	/**
	 * This method is called directly before the object is written to the database.
	 * Subclasses may override this method to execute code that should run before
	 * the object is stored to the DB. As the superclass-contructor stores
	 * unfinished objects to the DB we can ensure that certain things are done. For
	 * example subclasses initialize their tokenizer directly after the
	 * super-contructor is called. The super-constructor will put the unfinishes
	 * instance to the DB and another thread reads it again. It has no tokenizer yet
	 * and causes an error.
	 */
	protected void preDBHook() {
	}

	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(_wrapped);
	}
}