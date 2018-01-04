package command.client.get;

import server.TcpClient;
import server.Server;
import tokenizer.ITokenizable;
import util.ServerConst;

import com.fasterxml.jackson.core.JsonProcessingException;
import command.ClientCommand;
import command.Command;
import environment.entity.Dragon;
import environment.entity.Entity;
import environment.entity.Player;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerPlayer;

public class GetEntityCommand extends ClientCommand {

	public GetEntityCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITY);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int result = Command.PROCESSED;;
		_src.beginMessage();
		try {
			int id = Integer.parseInt(_cmd);
			Entity ent = Entity.getEntity(id);
			if(ent != null) {
				ITokenizable tok;
				if(ent instanceof Player) {
					tok = new ServerPlayer((Player)ent, server, false);
				} else {
					tok = new ServerDragon((Dragon)ent, server, false);
				}
				//_src.sendTokenizable(tok);
				_src.send(server.getObjectMapper().writeValueAsString(tok));
				_mes.append("sent entity "+id+" to "+_src);
			}
			else {
				_src.send(ServerConst.ANS+ServerConst.ANS_INVALID);
			}
		} catch(NumberFormatException | JsonProcessingException nfe) {
			_src.send(ServerConst.ANS+ServerConst.ANS_INVALID);
			result = Command.EXCEPTION;
		} finally {
			_src.endMessage();
		}
		return result;
	}
}
