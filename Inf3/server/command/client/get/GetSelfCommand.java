package command.client.get;

import java.util.Optional;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetSelfCommand extends ClientCommand {

	public GetSelfCommand(Server _server) {
		super(_server, ServerConst.GET_ME);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final Optional<String> json = _server.json(src.getPlayer().getWrappedObject());
		if (json.isPresent()) {
			src.send(json.get());
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
