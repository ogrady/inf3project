package command.client.ask.challenge;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

public class AskChallengeCommand extends ClientCommand {
	public AskChallengeCommand(Server _server) {
		super(_server, ServerConst.ASK_CHAL);
		addSubcommand(new AskSkirmishChallengeCommand(_server));
		//addSubcommand(new DragonChallengeCommand(_server));
		//addSubcommand(new StaghuntChallengeCommand(_server));
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
