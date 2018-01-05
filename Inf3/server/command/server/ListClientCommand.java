package command.server;

import java.net.Socket;
import java.util.List;

import output.Logger.MessageType;
import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class ListClientCommand extends ServerCommand {

	public ListClientCommand() {
		super(ServerConst.SC_LIST_CLIENT);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		String list = "";
		Socket sock;
		List<TcpClient> clients = src.getClients();
		for (TcpClient cl : clients) {
			sock = cl.getSocket();
			list += String.format("%s:%d - %s\r\n", sock.getInetAddress(), sock.getPort(),
					cl.getPlayer().getWrappedObject().toString());
		}
		src.getLogger().print(list, MessageType.INFO);
		mes.append(String.format("Succesfully listed %d clients", clients.size()));
		return 1;
	}
}
