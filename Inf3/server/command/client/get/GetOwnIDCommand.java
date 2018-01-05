package command.client.get;

import java.util.HashMap;
import java.util.Map;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetOwnIDCommand extends ClientCommand {

	public GetOwnIDCommand(Server server) {
		super(server, ServerConst.GET_MY_ID);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final Map<String, Integer> message = new HashMap<>();
		message.put(ServerConst.ANS_MY_ID, src.getPlayer().getWrappedObject().getId());
		src.send(_server.json(message).get());
		return Command.PROCESSED;
	}
}
