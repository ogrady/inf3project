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
	protected int routine(Server src, String cmd, StringBuilder mes) {
		int success = 1;
		try {
			src.getLogger().accept(MessageType.valueOf(cmd.toUpperCase()));
			mes.append(String.format("Successfully added '%s' to the sensitivity", cmd));
		} catch (IllegalArgumentException iae) {
			success = -1;
			mes.append(String.format("Could not add '%s' to the sensitivity", cmd));
		}
		return success;
	}
}
