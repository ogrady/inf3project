package environment.wrapper;

import server.Server;
import util.IWrapper;

abstract public class ServerWrapper<E> implements IWrapper<E> {
	protected E wrapped;
	protected Server server;

	@Override
	public E getWrappedObject() { return wrapped; }

	@Override
	public void setWrappedObject(E _wrapped) { wrapped = _wrapped; }

	public ServerWrapper(E _wrappedObject, Server _server) {
		wrapped = _wrappedObject; 
		server = _server;
	}
}
