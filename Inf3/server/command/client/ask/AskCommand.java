package command.client.ask;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

final public class AskCommand extends ClientCommand {
	public AskCommand(Server server) {
		super(server, ServerConst.CC_ASK);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
