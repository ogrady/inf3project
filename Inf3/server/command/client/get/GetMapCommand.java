package command.client.get;

import java.util.Optional;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetMapCommand extends ClientCommand {

	public GetMapCommand(Server server) {
		super(server, ServerConst.GET_MAP);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		// _src.flushTokenizable(server.getMap());
		final Optional<String> json = _server.json(_server.getMap().getWrappedObject());
		if (json.isPresent()) {
			src.send(json.get());
			mes.append("sent map to " + src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
