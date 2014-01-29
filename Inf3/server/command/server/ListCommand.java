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
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
