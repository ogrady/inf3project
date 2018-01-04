package environment.wrapper;

import java.util.List;

import server.Server;
import tokenizer.ITokenizable;
import tokenizer.Tokenizer;
import util.ServerConst;
import util.SyncedMap;
import environment.entity.Entity;
import environment.entity.Player;

abstract public class ServerEntity<E extends Entity> extends ServerWrapper<E> implements ITokenizable {
	// db here
	public static SyncedMap<ServerEntity<?>> instances = new SyncedMap<ServerEntity<?>>();
	protected Tokenizer<E> tokenizer;
	public boolean destructed;
	
	public ServerEntity(E _wrappedObject, Server _server, boolean _store) {
		super(_wrappedObject, _server);
		preDBHook();
		if(_store) {
			instances.put(_wrappedObject.getId(), this);
		}
	}
	
	/**
	 * Proxy to set the busy attribute of the wrapped {@link Player}. If calling the setter
	 * changed the attribute it will broadcast itself as long it is not disconnected.
	 * @param _busy
	 */
	public void setBusy(boolean _busy) {
		boolean former = wrapped.isBusy();
		wrapped.setBusy(_busy);
		if(!destructed && former != _busy) {
			server.broadcast(this.getWrappedObject(), ServerConst.UPD);
		}
	}
	
	public void destruct() {
		destructed = true;
		instances.remove(wrapped.getId());
		wrapped.destruct();
	}
	
	/**
	 * This method is called directly before the object is written to the database.
	 * Subclasses may override this method to execute code that should run before the object is stored to the DB.
	 * As the superclass-contructor stores unfinished objects to the DB we can ensure that certain things are done.
	 * For example subclasses initialize their tokenizer directly after the super-contructor is called. The super-constructor
	 * will put the unfinishes instance to the DB and another thread reads it again. It has no tokenizer yet and causes an error. 
	 */
	protected void preDBHook() {}

	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(wrapped);
	}
}