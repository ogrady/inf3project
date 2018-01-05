package command.client.ask;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.Message;
import util.ServerConst;

public class AskSayCommand extends ClientCommand {

	public AskSayCommand(Server _server) {
		super(_server, ServerConst.ASK_SAY);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		src.beginMessage();
		src.send(ServerConst.ANS + ServerConst.ANS_YES);
		src.endMessage();
		_server.broadcast(new Message(src.getPlayer().getWrappedObject().getId(),
				src.getPlayer().getWrappedObject().getDescription(), cmd));
		return Command.PROCESSED;
	}
}