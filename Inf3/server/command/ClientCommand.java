package command;

import server.TcpClient;
import server.Server;

abstract public class ClientCommand extends Command<TcpClient> {
	protected Server server;
	
	public ClientCommand(Server _server, String _sensitive) {
		super(_sensitive);
		server = _server;
	}
}
