package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.Optional;

import command.ClientCommand;
import command.Command;

public class GetMapCommand extends ClientCommand {

	public GetMapCommand(Server _server) {
		super(_server, ServerConst.GET_MAP);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		//_src.flushTokenizable(server.getMap());
		Optional<String> json = server.json(server.getMap().getWrappedObject());
		if(json.isPresent()) {
			_src.send(json.get());
			_mes.append("sent map to "+_src);
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}		
	}
}
