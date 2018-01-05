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
	protected int routine(Server src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
