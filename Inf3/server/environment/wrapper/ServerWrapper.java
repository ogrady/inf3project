package environment.wrapper;

import server.Server;
import util.IWrapper;

abstract public class ServerWrapper<E> implements IWrapper<E> {
	protected E _wrapped;
	protected Server _server;

	@Override
	public E getWrappedObject() {
		return _wrapped;
	}

	@Override
	public void setWrappedObject(E wrapped) {
		_wrapped = wrapped;
	}

	public ServerWrapper(E wrappedObject, Server server) {
		_wrapped = wrappedObject;
		_server = server;
	}
}
