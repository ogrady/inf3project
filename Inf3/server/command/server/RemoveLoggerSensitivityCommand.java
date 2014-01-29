package command.server;

import output.Logger.MessageType;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class RemoveLoggerSensitivityCommand extends ServerCommand {

	public RemoveLoggerSensitivityCommand() {
		super(ServerConst.SC_LOGGER_REMOVE);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		int success = 1;
		try {
			_src.getLogger().dismiss(MessageType.valueOf(_cmd.toUpperCase()));
			_mes.append(String.format("Successfully removed '%s' from the sensitivity", _cmd));
		} catch(IllegalArgumentException iae) {
			success = -1;
			_mes.append(String.format("Could not remove '%s' from the sensitivity", _cmd));
		}
		return success;
	}
}
