package command.client.get;

import java.util.HashMap;
import java.util.Map;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetOnlineCountCommand extends ClientCommand {

	public GetOnlineCountCommand(Server _server) {
		super(_server, ServerConst.GET_COUNT);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final Map<String, Integer> message = new HashMap<>();
		message.put(ServerConst.ANS_COUNT, _server.getClients().size());
		src.send(_server.json(message).get());
		mes.append("sent number of online users to " + src);
		return Command.PROCESSED;
	}
}
