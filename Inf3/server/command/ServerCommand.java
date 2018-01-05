package command;

import server.Server;

abstract public class ServerCommand extends Command<Server> {
	public ServerCommand(String sensitive) {
		super(sensitive);
	}
}
