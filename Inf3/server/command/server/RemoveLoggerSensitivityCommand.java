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
	protected int routine(Server src, String cmd, StringBuilder mes) {
		int success = 1;
		try {
			src.getLogger().dismiss(MessageType.valueOf(cmd.toUpperCase()));
			mes.append(String.format("Successfully removed '%s' from the sensitivity", cmd));
		} catch (IllegalArgumentException iae) {
			success = -1;
			mes.append(String.format("Could not remove '%s' from the sensitivity", cmd));
		}
		return success;
	}
}
