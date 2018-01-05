package command.client.ask.set;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;
import command.Command;
import environment.entity.SkirmishDecision;

/**
 * Tries to set the skirmish-decision of the sending player to something
 * else.<br>
 * The new value must be valid or the request will be declined.
 * 
 * @author Daniel
 */
public class AskSetSkirmishCommand extends ClientCommand {
	public AskSetSkirmishCommand(Server server) {
		super(server, ServerConst.SET_SKIRMISH);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int success = Command.PROCESSED;
		try {
			src.getPlayer().getWrappedObject().setSkirmishDecision(SkirmishDecision.valueOf(cmd.toUpperCase()));
		} catch (IllegalArgumentException iae) {
			success = Command.EXCEPTION;
		}
		return success;
	}
}
