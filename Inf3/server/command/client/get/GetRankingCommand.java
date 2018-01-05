package command.client.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import command.ClientCommand;
import command.Command;
import environment.entity.Player;
import server.Server;
import server.TcpClient;
import util.Const;
import util.ServerConst;

public class GetRankingCommand extends ClientCommand {

	public GetRankingCommand(Server _server) {
		super(_server, ServerConst.GET_RANKING);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final List<Player> players = _server.getClients().stream().map(c -> c.getPlayer().getWrappedObject())
				.sorted((Player p1, Player p2) -> p2.getPoints() - p1.getPoints()).collect(Collectors.toList());

		final Map<String, List<Player>> ranking = new HashMap<>();
		ranking.put(Const.PAR_RANKING, players);
		src.send(_server.json(ranking).get());
		mes.append("sent ranking to " + src);
		return Command.PROCESSED;
	}
}
