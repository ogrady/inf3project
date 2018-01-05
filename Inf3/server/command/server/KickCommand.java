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
	protected int routine(Server src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
