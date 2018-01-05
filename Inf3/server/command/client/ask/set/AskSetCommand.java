package command.client.ask.set;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;
import command.Command;

public class AskSetCommand extends ClientCommand {

	public AskSetCommand(Server server) {
		super(server, ServerConst.ASK_SET);
		addSubcommand(new AskSetDragonCommand(server));
		addSubcommand(new AskSetSkirmishCommand(server));
		addSubcommand(new AskSetStaghuntCommand(server));
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int success = executeSubcommands(src, cmd, mes);
		if (success == Command.PROCESSED) {
			src.sendOk();
		} else {
			src.sendNo();
		}
		return success;
	}
}
