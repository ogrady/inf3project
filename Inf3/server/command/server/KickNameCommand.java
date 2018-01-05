package command.server;

import java.util.Iterator;

import server.TcpClient;
import server.Server;
import util.ServerConst;
import util.ServerMessage;

import command.ServerCommand;

public class KickNameCommand extends ServerCommand {

	public KickNameCommand() {
		super(ServerConst.SC_KICK_NAME);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		int result = 0;
		TcpClient client = null, next;
		Iterator<TcpClient> it = src.getClients().iterator();
		while (it.hasNext() && client == null) {
			next = it.next();
			if (cmd.equals(next.getPlayer().getWrappedObject().getDescription())) {
				client = next;
			}
		}
		if (client != null) {
			result = 1;
			mes.append(String.format(String.format("Sucessfully kicked '%s'\r\n", cmd)));
			client.flushTokenizable(new ServerMessage("you were kicked from the server"));
			client.close();
		} else {
			result = -1;
			mes.append(String.format(String.format("Could not find player named '%s'\r\n", cmd)));
		}
		return result;
	}

}
