package command.client.ask;

import server.TcpClient;
import server.Server;
import util.Message;
import util.ServerConst;

import command.ClientCommand;
import command.Command;

public class AskSayCommand extends ClientCommand {

	public AskSayCommand(Server _server) {
		super(_server, ServerConst.ASK_SAY);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.beginMessage();
		_src.send(ServerConst.ANS+ServerConst.ANS_YES);
		_src.endMessage();
		server.broadcast(new Message(_src.getPlayer().getWrappedObject().getId(), _src.getPlayer().getWrappedObject().getDescription(), _cmd));
		return Command.PROCESSED;
	}
}