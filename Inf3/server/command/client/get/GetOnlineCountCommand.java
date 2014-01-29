package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class GetOnlineCountCommand extends ClientCommand {

	public GetOnlineCountCommand(Server _server) {
		super(_server, ServerConst.GET_COUNT);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.beginMessage();
		_src.send(ServerConst.BEGIN+ServerConst.ANS_COUNT);
		_src.send(""+server.getClients().size());
		_src.send(ServerConst.END+ServerConst.ANS_COUNT);
		_src.endMessage();
		_mes.append("sent number of online users to "+_src);
		return 1;
	}
}
