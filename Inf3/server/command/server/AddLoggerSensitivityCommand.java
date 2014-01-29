package command.server;

import output.Logger.MessageType;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class AddLoggerSensitivityCommand extends ServerCommand {

	public AddLoggerSensitivityCommand() {
		super(ServerConst.SC_LOGGER_ADD);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		int success = 1;
		try {
			_src.getLogger().accept(MessageType.valueOf(_cmd.toUpperCase()));
			_mes.append(String.format("Successfully added '%s' to the sensitivity", _cmd));
		} catch(IllegalArgumentException iae) {
			success = -1;
			_mes.append(String.format("Could not add '%s' to the sensitivity", _cmd));
		}
		return success;
	}
}
