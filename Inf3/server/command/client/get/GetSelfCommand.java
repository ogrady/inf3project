package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class GetSelfCommand extends ClientCommand {

	public GetSelfCommand(Server _server) {
		super(_server, ServerConst.GET_ME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.flushTokenizable(_src.getPlayer());
		_mes.append("sent own entity to "+_src);
		return 1;
	}
}
