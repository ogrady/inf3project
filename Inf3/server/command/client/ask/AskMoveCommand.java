package command.client.ask;

import server.TcpClient;
import server.Server;
import util.ServerConst;
import util.Vector2D;

import command.ClientCommand;
import command.Command;
import environment.MapCell;
import environment.Property;

/**
 * Tries to move on the map.<br>
 * This command succeeds if the executing player is not busy and tries to move to a walkable cell, adjacent to his current position.
 * @author Daniel
 */
public class AskMoveCommand extends ClientCommand {
	public AskMoveCommand(Server _server) {
		super(_server, ServerConst.ASK_MOVE);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int result = Command.NOT_RESPONSIBLE;
		Vector2D pos = _src.getPlayer().getWrappedObject().getPosition();
		int x = pos.x;
		int y = pos.y;
		if(_cmd.equals(ServerConst.MOVE_UP)) {
			y--; 
		}
		else if(_cmd.equals(ServerConst.MOVE_DOWN)) {
			y++;
		}
		else if(_cmd.equals(ServerConst.MOVE_LEFT)) {
			x--;
		}
		else if(_cmd.equals(ServerConst.MOVE_RIGHT)) {
			x++;
		}
		else {
			result = Command.EXCEPTION;
		}
		if(result != Command.EXCEPTION) {
			MapCell cell = server.getMap().getWrappedObject().getCellAt(x, y);
			String answer = ServerConst.ANS_NO;
			if(!_src.getPlayer().getWrappedObject().isBusy() && cell != null && cell.hasProperty(Property.WALKABLE)) {
				answer = ServerConst.ANS_YES;
				pos.x = x;
				pos.y = y;
			}
			_src.beginMessage();
			_src.send(ServerConst.ANS+answer);
			_src.endMessage();
			if(answer.equals(ServerConst.ANS_YES)) {
				server.broadcast(_src.getPlayer().getWrappedObject(), ServerConst.UPD);
			}
			result = Command.PROCESSED;
		}
		return result;
	}
}
