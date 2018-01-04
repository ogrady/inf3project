package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.Optional;

import command.ClientCommand;
import command.Command;

public class GetSelfCommand extends ClientCommand {

	public GetSelfCommand(Server _server) {
		super(_server, ServerConst.GET_ME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		Optional<String> json = server.json(_src.getPlayer().getWrappedObject());
		if(json.isPresent()) {
			_src.send(json.get());
			return Command.PROCESSED;
		} else {
			return Command.EXCEPTION;
		}
	}
}
