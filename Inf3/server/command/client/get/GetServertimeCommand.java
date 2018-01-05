package command.client.get;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetServertimeCommand extends ClientCommand {

	public GetServertimeCommand(Server _server) {
		super(_server, ServerConst.GET_TIME);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final Map<String, Long> message = new HashMap<>();
		message.put(ServerConst.ANS_TIME, System.currentTimeMillis());

		final Optional<String> json = _server.json(message);
		if (json.isPresent()) {
			src.send(_server.json(message).get());
			mes.append("sent servertime to " + src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
