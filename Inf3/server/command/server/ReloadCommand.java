package command.server;

import server.Server;
import util.Configuration;
import util.ServerConst;

import command.ServerCommand;

/**
 * Attempts to reload the serverconfig from the file as specified in {@link Configuration}.
 * @author Daniel
 */
public class ReloadCommand extends ServerCommand {
	public ReloadCommand() {
		super(ServerConst.SC_RELOAD);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		boolean success = Configuration.getInstance().load();
		int result = 1;
		if(success) {
			_mes.append("reloaded the server config");
		} else {
			result = -1;
			_mes.append("could not reload the server config");
		}
		return result;
	}
}