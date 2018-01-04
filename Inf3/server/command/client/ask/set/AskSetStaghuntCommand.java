package command.client.ask.set;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;
import command.Command;
import environment.entity.StaghuntDecision;

/**
 * Tries to set the staghunt-decision of the sending player to something else.<br>
 * The new value must be valid or the request will be declined.
 * @author Daniel
 */
public class AskSetStaghuntCommand extends ClientCommand {
	public AskSetStaghuntCommand(Server _server) {
		super(_server, ServerConst.SET_STAGHUNT);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int success = Command.PROCESSED;
		try {
			_src.getPlayer().getWrappedObject().setStaghuntDecision(StaghuntDecision.valueOf(_cmd.toUpperCase()));
		} catch(IllegalArgumentException iae) {
			success = Command.EXCEPTION;
		}
		return success;
	}
}
