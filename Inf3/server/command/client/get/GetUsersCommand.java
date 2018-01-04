package command.client.get;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;
import command.Command;
import environment.wrapper.ServerPlayer;

public class GetUsersCommand extends ClientCommand {

	public GetUsersCommand(Server _server) {
		super(_server, ServerConst.GET_USERS);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		List<ServerPlayer> players = new ArrayList<>();
		Iterator<TcpClient> it = server.getClients().iterator();
		while(it.hasNext()) {
			players.add(it.next().getPlayer());
		}
		
		Optional<String> json = server.json(players);
		if(json.isPresent()) {
			_src.send(json.get());
			_mes.append("sent all users to "+_src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
