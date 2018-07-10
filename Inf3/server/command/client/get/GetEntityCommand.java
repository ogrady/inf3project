package command.client.get;

import java.util.HashMap;
import java.util.Optional;

import command.ClientCommand;
import command.Command;
import environment.entity.Entity;
import server.Server;
import server.TcpClient;
import util.Const;
import util.ServerConst;

public class GetEntityCommand extends ClientCommand {

	public GetEntityCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITY);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int result = Command.PROCESSED;
		try {
			final int id = Integer.parseInt(cmd);
			final Entity ent = Entity.getEntity(id);
			if (ent != null) {
				final HashMap<String, Entity> response = new HashMap<>();
				response.put(Const.PAR_ENTITY, ent);
				final Optional<String> json = _server.json(response);
				if (json.isPresent()) {
					src.send(json.get());
					mes.append("sent entity " + id + " to " + src);
				}
			} else {
				src.sendInvalid();
			}
		} catch (NumberFormatException nfe) {
			src.send(ServerConst.ANS + ServerConst.ANS_INVALID);
			result = Command.EXCEPTION;
		}
		return result;
	}
}
