package command.client.ask.set;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class AskSetCommand extends ClientCommand {

	public AskSetCommand(Server _server) {
		super(_server, ServerConst.ASK_SET);
		addSubcommand(new AskSetDragonCommand(_server));
		addSubcommand(new AskSetSkirmishCommand(_server));
		addSubcommand(new AskSetStaghuntCommand(_server));
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int success = executeSubcommands(_src, _cmd, _mes);
		if(success == 1) {
			_src.sendOk();
		} else {
			_src.sendNo();
		}
		return success;
	}
}
