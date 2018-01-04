package command.client.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import server.TcpClient;
import server.Server;
import util.Const;
import util.ServerConst;

import command.ClientCommand;
import command.Command;
import environment.entity.Entity;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerPlayer;

public class GetAllEntitesCommand extends ClientCommand {

	public GetAllEntitesCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITIES);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int cnt = ServerPlayer.instances.size() + ServerDragon.instances.size();
		Map<String, List<Entity>> entities = new HashMap<>();
		
		entities.put(Const.PAR_PLAYERS,
				ServerPlayer.instances.values().stream().map(e->e.getWrappedObject()).collect(Collectors.toList()));
		
		entities.put(Const.PAR_DRAGONS,
				ServerDragon.instances.values().stream().map(e->e.getWrappedObject()).collect(Collectors.toList()));
		_src.send(server.json(entities).get());
		_mes.append(String.format("sent %d entities to %s", cnt, _src.toString()));
		return Command.PROCESSED;
	}
}
