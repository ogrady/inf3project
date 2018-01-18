package command.client.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import command.ClientCommand;
import command.Command;
import environment.entity.Entity;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerPlayer;
import server.Server;
import server.TcpClient;
import util.Const;
import util.ServerConst;

public class GetAllEntitesCommand extends ClientCommand {

	public GetAllEntitesCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITIES);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final int cnt = ServerPlayer.instances.size() + ServerDragon.instances.size();
		final Map<String, Map<String, List<Entity>>> outer = new HashMap<>();
		final Map<String, List<Entity>> entities = new HashMap<>();

		entities.put(Const.PAR_PLAYERS,
				ServerPlayer.instances.values().stream().map(e -> e.getWrappedObject()).collect(Collectors.toList()));

		entities.put(Const.PAR_DRAGONS,
				ServerDragon.instances.values().stream().map(e -> e.getWrappedObject()).collect(Collectors.toList()));
		outer.put(Const.PAR_ENTITIES, entities);
		src.send(_server.json(outer).get());
		mes.append(String.format("sent %d entities to %s", cnt, src.toString()));
		return Command.PROCESSED;
	}
}
