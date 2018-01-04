package command.client.ask;

import output.Logger.MessageType;
import server.TcpClient;
import server.Server;
import util.Message;
import util.ServerConst;

import command.ClientCommand;
import command.Command;

public class AskDisconnectCommand extends ClientCommand {

	public AskDisconnectCommand(Server _server) {
		super(_server, ServerConst.ASK_BYE);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		_src.send(ServerConst.ANS+ServerConst.ANS_YES);
		_src.close();
		server.broadcast(new Message(-1, ServerConst.SERVER, _src.getPlayer().getWrappedObject().getDescription()+" disconnected"));
		server.getLogger().println(String.format("Disconnecting %s on own behalf", _src, MessageType.OUTPUT));
		return Command.PROCESSED;
	}
}
