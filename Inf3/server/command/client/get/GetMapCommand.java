package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class GetMapCommand extends ClientCommand {

	public GetMapCommand(Server _server) {
		super(_server, ServerConst.GET_MAP);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.flushTokenizable(server.getMap());
		_mes.append("sent map to "+_src);
		return 1;
	}
}
