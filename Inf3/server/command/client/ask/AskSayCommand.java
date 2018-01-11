package command.client.ask;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.Const;
import util.Message;
import util.ServerConst;

public class AskSayCommand extends ClientCommand {

	public AskSayCommand(Server _server) {
		super(_server, ServerConst.ASK_SAY);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		src.sendOk();
		_server.broadcast(new Message(src.getPlayer().getWrappedObject().getId(),
				src.getPlayer().getWrappedObject().getDescription(), cmd), Const.PAR_MESSAGE);
		return Command.PROCESSED;
	}
}