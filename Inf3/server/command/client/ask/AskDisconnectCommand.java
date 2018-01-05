package command.client.ask;

import command.ClientCommand;
import command.Command;
import output.Logger.MessageType;
import server.Server;
import server.TcpClient;
import util.Message;
import util.ServerConst;

public class AskDisconnectCommand extends ClientCommand {

	public AskDisconnectCommand(Server server) {
		super(server, ServerConst.ASK_BYE);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		src.send(ServerConst.ANS + ServerConst.ANS_YES);
		src.close();
		_server.broadcast(new Message(-1, ServerConst.SERVER,
				src.getPlayer().getWrappedObject().getDescription() + " disconnected"));
		_server.getLogger().println(String.format("Disconnecting %s on own behalf", src, MessageType.OUTPUT));
		return Command.PROCESSED;
	}
}
