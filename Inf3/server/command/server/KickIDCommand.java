package command.server;

import java.util.Iterator;

import server.TcpClient;
import server.Server;
import util.Const;
import util.ServerConst;
import util.ServerMessage;

import command.ServerCommand;

public class KickIDCommand extends ServerCommand {

	public KickIDCommand() {
		super(ServerConst.SC_KICK_ID);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		int result = 0;
		TcpClient client = null, next;
		try {
			int id = Integer.parseInt(cmd);
			Iterator<TcpClient> it = src.getClients().iterator();
			while (it.hasNext() && client == null) {
				next = it.next();
				if (id == next.getPlayer().getWrappedObject().getId()) {
					client = next;
				}
			}
			if (client != null) {
				result = 1;
				mes.append(String.format("Sucessfully kicked player with ID '%s'\r\n", cmd));
				client.send(src.json(Const.PAR_MESSAGE, new ServerMessage("you were kicked from the server")).get());
				client.close();
			} else {
				result = -1;
				mes.append(String.format("Could not find player with ID '%s'\r\n", cmd));
			}
		} catch (NumberFormatException nfe) {
			mes.append(String.format("Invalid ID: '%s'\r\n", cmd));
			result = -1;
		}
		return result;
	}
}
