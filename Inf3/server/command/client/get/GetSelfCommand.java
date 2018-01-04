package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import com.fasterxml.jackson.core.JsonProcessingException;
import command.ClientCommand;

public class GetSelfCommand extends ClientCommand {

	public GetSelfCommand(Server _server) {
		super(_server, ServerConst.GET_ME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		try {
			_src.send(server.getObjectMapper().writeValueAsString(_src.getPlayer()));
			return 1;
		} catch (JsonProcessingException e) {
			return -1;
		}
		/*_src.flushTokenizable(_src.getPlayer());
		_mes.append("sent own entity to "+_src);
		return 1;*/
	}
}
