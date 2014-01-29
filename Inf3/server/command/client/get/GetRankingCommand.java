package command.client.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import server.TcpClient;
import server.Server;
import util.ServerConst;
import util.ServerMessage;

import command.ClientCommand;

import environment.entity.Player;

public class GetRankingCommand extends ClientCommand {

	public GetRankingCommand(Server _server) {
		super(_server, ServerConst.GET_RANKING);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		ArrayList<Player> players = new ArrayList<Player>(server.getClients().size());
		for(TcpClient cl : server.getClients()) {
			players.add(cl.getPlayer().getWrappedObject());
		}
		Collections.sort(players, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				return p2.getPoints() - p1.getPoints();
			}
		});
		for(int i=0; i<players.size(); i++) {
			_src.flushTokenizable(new ServerMessage(String.format("%d. %s (%d)", i+1, players.get(i).getDescription(), players.get(i).getPoints())));
		}
		_mes.append("sent ranking to "+_src);
		return 1;
	}
}
