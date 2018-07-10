package command.client.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import command.ClientCommand;
import command.Command;
import environment.entity.Player;
import server.Server;
import server.TcpClient;
import util.ServerConst;

public class GetUsersCommand extends ClientCommand {

	public GetUsersCommand(Server _server) {
		super(_server, ServerConst.GET_USERS);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		Map<String, List<Player>> response = new HashMap<>();
		// is this threadsafe?
		/*
		final Iterator<TcpClient> it = _server.getClients().iterator();
		while (it.hasNext()) {
			players.add(it.next().getPlayer());
		}
		 */
		response.put(ServerConst.ANS_USERS, 
				_server.getClients().stream().map(c -> c.getPlayer().getWrappedObject()).collect(Collectors.toList())
		);
		final Optional<String> json = _server.json(response);
		if (json.isPresent()) {
			src.send(json.get());
			mes.append("sent all users to " + src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
