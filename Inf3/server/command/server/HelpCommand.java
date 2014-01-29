package command.server;

import output.Logger.MessageType;
import server.Server;
import util.ServerConst;

import command.ServerCommand;
import command.TopLevelCommand;

/**
 * Lists all registered server-commands.
 * @author Daniel
 *
 */
public class HelpCommand extends ServerCommand {
	public HelpCommand() {
		super(ServerConst.SC_HELP);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		TopLevelCommand<Server> servercommands = _src.getServerCommands();
		String cmds = servercommands.toString();
		_src.getLogger().println(cmds, MessageType.INFO);
		_mes.append(String.format("Detected %d active servercommands", cmds.split("\r\n").length));
		return 1;
	}
}