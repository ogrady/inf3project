package command.server;

import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class ShutdownCommand extends ServerCommand {

	public ShutdownCommand() {
		super(ServerConst.SC_SHUTDOWN);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		src.stop();
		return 1;
	}
}
