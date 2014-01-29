package command.client.get;

import java.util.Iterator;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class GetUsersCommand extends ClientCommand {

	public GetUsersCommand(Server _server) {
		super(_server, ServerConst.GET_USERS);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.beginMessage();
		_src.send(ServerConst.BEGIN+ServerConst.ANS_USERS);
		Iterator<TcpClient> it = server.getClients().iterator();
		while(it.hasNext()) {
			_src.sendTokenizable(it.next().getPlayer());
		}
		_src.send(ServerConst.END+ServerConst.ANS_USERS);
		_src.endMessage();
		_mes.append("sent all users to "+_src);
		return 1;
	}
}
