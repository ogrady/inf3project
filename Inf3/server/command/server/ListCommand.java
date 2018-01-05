package command.server;

import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class ListCommand extends ServerCommand {

	public ListCommand() {
		super(ServerConst.SC_LIST);
		addSubcommand(new ListPlayerCommand());
		addSubcommand(new ListClientCommand());
		addSubcommand(new ListEntitiesCommand());
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
