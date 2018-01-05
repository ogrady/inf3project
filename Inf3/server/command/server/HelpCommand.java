package command.server;

import output.Logger.MessageType;
import server.Server;
import util.ServerConst;

import command.ServerCommand;
import command.TopLevelCommand;

/**
 * Lists all registered server-commands.
 * 
 * @author Daniel
 *
 */
public class HelpCommand extends ServerCommand {
	public HelpCommand() {
		super(ServerConst.SC_HELP);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		TopLevelCommand<Server> servercommands = src.getServerCommands();
		String cmds = servercommands.toString();
		src.getLogger().println(cmds, MessageType.INFO);
		mes.append(String.format("Detected %d active servercommands", cmds.split("\r\n").length));
		return 1;
	}
}