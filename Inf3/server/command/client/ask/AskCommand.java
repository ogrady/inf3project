package command.client.ask;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

final public class AskCommand extends ClientCommand {
	public AskCommand(Server _server) {
		super(_server, ServerConst.CC_ASK);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
