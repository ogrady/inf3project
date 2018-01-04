package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.HashMap;
import java.util.Map;

import command.ClientCommand;
import command.Command;

public class GetOnlineCountCommand extends ClientCommand {

	public GetOnlineCountCommand(Server _server) {
		super(_server, ServerConst.GET_COUNT);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		Map<String,Integer> mes = new HashMap<>();
		mes.put(ServerConst.ANS_COUNT, server.getClients().size());
		_src.send(server.json(mes).get());
		_mes.append("sent number of online users to "+_src);
		return Command.PROCESSED;
	}
}
