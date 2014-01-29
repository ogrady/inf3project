package command.server;

import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class LoggerSensitivityCommand extends ServerCommand {

	public LoggerSensitivityCommand() {
		super(ServerConst.SC_LOGGER);
		addSubcommand(new AddLoggerSensitivityCommand());
		addSubcommand(new RemoveLoggerSensitivityCommand());
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
