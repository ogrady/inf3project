package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.HashMap;
import java.util.Map;

import command.ClientCommand;
import command.Command;

public class GetOwnIDCommand extends ClientCommand {

	public GetOwnIDCommand(Server _server) {
		super(_server, ServerConst.GET_MY_ID);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		Map<String, Integer> mes = new HashMap<>();
		mes.put(ServerConst.ANS_MY_ID, _src.getPlayer().getWrappedObject().getId());
		_src.send(server.json(mes).get());
		return Command.PROCESSED;
	}
}
