package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class GetOwnIDCommand extends ClientCommand {

	public GetOwnIDCommand(Server _server) {
		super(_server, ServerConst.GET_MY_ID);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.beginMessage();
		_src.send(ServerConst.BEGIN+ServerConst.ANS_MY_ID);
		_src.send(""+_src.getPlayer().getWrappedObject().getId());
		_src.send(ServerConst.END+ServerConst.ANS_MY_ID);
		_src.endMessage();
		_mes.append("sent own entity id to "+_src);
		return 1;
	}
}
