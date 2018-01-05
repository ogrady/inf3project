package command.server;

import java.util.Iterator;

import output.Logger.MessageType;
import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ServerCommand;

public class InfoCommand extends ServerCommand {

	public InfoCommand() {
		super(ServerConst.SC_INFO);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		int result = 1;
		try {
			int id = Integer.parseInt(cmd);
			TcpClient next, client = null;
			Iterator<TcpClient> it = src.getClients().iterator();
			while (it.hasNext()) {
				next = it.next();
				if (id == next.getPlayer().getWrappedObject().getId()) {
					client = next;
				}
			}
			if (client == null) {
				mes.append(String.format("No client with ID %s", cmd));
				result = -1;
			} else {
				src.getLogger().println(client.getPlayer().getWrappedObject().toVerboseString(), MessageType.INFO);
			}
		} catch (NumberFormatException nfe) {
			mes.append(String.format("Tried to retreive info for invalid ID '%s'", cmd));
			result = -1;
		}
		return result;
	}

}
