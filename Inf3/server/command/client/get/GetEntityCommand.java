package command.client.get;

import com.fasterxml.jackson.core.JsonProcessingException;

import command.ClientCommand;
import command.Command;
import environment.entity.Dragon;
import environment.entity.Entity;
import environment.entity.Player;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerPlayer;
import server.Server;
import server.TcpClient;
import tokenizer.ITokenizable;
import util.ServerConst;

public class GetEntityCommand extends ClientCommand {

	public GetEntityCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITY);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int result = Command.PROCESSED;
		;
		src.beginMessage();
		try {
			final int id = Integer.parseInt(cmd);
			final Entity ent = Entity.getEntity(id);
			if (ent != null) {
				ITokenizable tok;
				tok = ent instanceof Player ? new ServerPlayer((Player) ent, _server, false)
						: new ServerDragon((Dragon) ent, _server, false);
				src.send(_server.getObjectMapper().writeValueAsString(tok));
				mes.append("sent entity " + id + " to " + src);
			} else {
				src.send(ServerConst.ANS + ServerConst.ANS_INVALID);
			}
		} catch (NumberFormatException | JsonProcessingException nfe) {
			src.send(ServerConst.ANS + ServerConst.ANS_INVALID);
			result = Command.EXCEPTION;
		} finally {
			src.endMessage();
		}
		return result;
	}
}
