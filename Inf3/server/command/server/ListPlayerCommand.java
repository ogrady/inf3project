package command.server;

import java.util.List;

import output.Logger.MessageType;
import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

import environment.entity.Player;

public class ListPlayerCommand extends ServerCommand {

	public ListPlayerCommand() {
		super(ServerConst.SC_LIST_PLAYER);
	}

	@Override
	protected int routine(Server _src, String _cmd, StringBuilder _mes) {
		String list = "";
		Player pl;
		List<TcpClient> clients = _src.getClients();
		for(TcpClient cl : clients) {
			pl = cl.getPlayer().getWrappedObject();
			list += String.format("%d: %s\r\n",pl.getId(),pl.getDescription());
		}
		_src.getLogger().print(list, MessageType.INFO);
		_mes.append(String.format("Succesfully listed %d players", clients.size()));
		return 1;
	}
}
