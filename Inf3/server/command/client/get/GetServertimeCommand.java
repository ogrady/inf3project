package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import command.ClientCommand;
import command.Command;

public class GetServertimeCommand extends ClientCommand {

	public GetServertimeCommand(Server _server) {
		super(_server, ServerConst.GET_TIME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		Map<String,Long> mes = new HashMap<>();
		mes.put(ServerConst.ANS_TIME, System.currentTimeMillis());
		
		Optional<String> json = server.json(mes);
		if(json.isPresent()) {
			_src.send(server.json(mes).get());
			_mes.append("sent servertime to "+_src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
