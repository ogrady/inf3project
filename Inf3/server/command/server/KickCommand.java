package command.server;

import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class KickCommand extends ServerCommand {
	public KickCommand() {
		super(ServerConst.SC_KICK);
		addSubcommand(new KickNameCommand());
		addSubcommand(new KickIDCommand());
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes); 
	}
}
