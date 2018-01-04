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
 * @author Daniel
 */
public class AskSetDragonCommand extends ClientCommand {
	public AskSetDragonCommand(Server _server) {
		super(_server, ServerConst.SET_DRAGON);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int success = Command.PROCESSED;
		try {
			_src.getPlayer().getWrappedObject().setDragonDecision(DragonDecision.valueOf(_cmd.toUpperCase()));
		} catch(IllegalArgumentException iae) {
			success = Command.EXCEPTION;
		}
		return success;
	}
}
