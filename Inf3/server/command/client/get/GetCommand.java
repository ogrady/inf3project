package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

final public class GetCommand extends ClientCommand {
	public GetCommand(Server server) {
		super(server, ServerConst.CC_GET);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
