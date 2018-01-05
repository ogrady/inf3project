package command.client.ask;

import command.ClientCommand;
import command.Command;
import environment.MapCell;
import environment.Property;
import server.Server;
import server.TcpClient;
import util.ServerConst;
import util.Vector2D;

/**
 * Tries to move on the map.<br>
 * This command succeeds if the executing player is not busy and tries to move
 * to a walkable cell, adjacent to his current position.
 * 
 * @author Daniel
 */
public class AskMoveCommand extends ClientCommand {
	public AskMoveCommand(Server _server) {
		super(_server, ServerConst.ASK_MOVE);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int result = Command.NOT_RESPONSIBLE;
		final Vector2D pos = src.getPlayer().getWrappedObject().getPosition();
		int x = pos.x;
		int y = pos.y;
		if (cmd.equals(ServerConst.MOVE_UP)) {
			y--;
		} else if (cmd.equals(ServerConst.MOVE_DOWN)) {
			y++;
		} else if (cmd.equals(ServerConst.MOVE_LEFT)) {
			x--;
		} else if (cmd.equals(ServerConst.MOVE_RIGHT)) {
			x++;
		} else {
			result = Command.EXCEPTION;
		}
		if (result != Command.EXCEPTION) {
			final MapCell cell = _server.getMap().getWrappedObject().getCellAt(x, y);
			String answer = ServerConst.ANS_NO;
			if (!src.getPlayer().getWrappedObject().isBusy() && cell != null && cell.hasProperty(Property.WALKABLE)) {
				answer = ServerConst.ANS_YES;
				pos.x = x;
				pos.y = y;
			}
			src.beginMessage();
			src.send(ServerConst.ANS + answer);
			src.endMessage();
			if (answer.equals(ServerConst.ANS_YES)) {
				_server.broadcast(src.getPlayer().getWrappedObject(), ServerConst.UPD);
			}
			result = Command.PROCESSED;
		}
		return result;
	}
}
