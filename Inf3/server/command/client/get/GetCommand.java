package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

final public class GetCommand extends ClientCommand {
	public GetCommand(Server _server) {
		super(_server, ServerConst.CC_GET);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
