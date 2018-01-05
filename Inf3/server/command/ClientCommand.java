package command;

import server.Server;
import server.TcpClient;

abstract public class ClientCommand extends Command<TcpClient> {
	protected Server _server;

	public ClientCommand(Server server, String sensitive) {
		super(sensitive);
		_server = server;
	}
}
