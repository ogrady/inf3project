package command.client.get;

import java.util.HashMap;
import java.util.Optional;

import command.ClientCommand;
import command.Command;
import environment.entity.Player;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetSelfCommand extends ClientCommand {

	public GetSelfCommand(Server _server) {
		super(_server, ServerConst.GET_ME);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final HashMap<String, Player> response = new HashMap<>();
		response.put(ServerConst.ANS_ME, src.getPlayer().getWrappedObject());
		final Optional<String> json = _server.json(response);
		if (json.isPresent()) {
			src.send(json.get());
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
