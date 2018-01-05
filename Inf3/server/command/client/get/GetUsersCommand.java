package command.client.get;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import command.ClientCommand;
import command.Command;
import environment.wrapper.ServerPlayer;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetUsersCommand extends ClientCommand {

	public GetUsersCommand(Server _server) {
		super(_server, ServerConst.GET_USERS);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final List<ServerPlayer> players = new ArrayList<>();
		final Iterator<TcpClient> it = _server.getClients().iterator();
		while (it.hasNext()) {
			players.add(it.next().getPlayer());
		}

		final Optional<String> json = _server.json(players);
		if (json.isPresent()) {
			src.send(json.get());
			mes.append("sent all users to " + src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
