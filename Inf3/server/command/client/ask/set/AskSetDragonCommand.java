package command.client.ask.set;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;
import command.Command;
import environment.entity.DragonDecision;

/**
 * Tries to set the dragon-decision of the sending player to something else.<br>
 * The new value must be valid or the request will be declined.
 * 
 * @author Daniel
 */
public class AskSetDragonCommand extends ClientCommand {
	public AskSetDragonCommand(Server server) {
		super(server, ServerConst.SET_DRAGON);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int success = Command.PROCESSED;
		try {
			src.getPlayer().getWrappedObject().setDragonDecision(DragonDecision.valueOf(cmd.toUpperCase()));
		} catch (IllegalArgumentException iae) {
			success = Command.EXCEPTION;
		}
		return success;
	}
}
