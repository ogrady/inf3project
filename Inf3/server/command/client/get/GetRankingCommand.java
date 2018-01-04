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
import environment.entity.Player;

public class GetRankingCommand extends ClientCommand {

	public GetRankingCommand(Server _server) {
		super(_server, ServerConst.GET_RANKING);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		List<Player> players = server.getClients().stream()
				.map(c -> c.getPlayer().getWrappedObject())
				.sorted((Player p1, Player p2) -> p2.getPoints() - p1.getPoints())
				.collect(Collectors.toList());
				
		Map<String, List<Player>> ranking = new HashMap<>();
		ranking.put(Const.PAR_RANKING, players);
		_src.send(server.json(ranking).get());
		_mes.append("sent ranking to " + _src);
		return Command.PROCESSED;
	}
}
