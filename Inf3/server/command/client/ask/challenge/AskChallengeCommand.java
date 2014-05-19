package command.client.ask.challenge;

import server.Server;
import server.TcpClient;
import util.ServerConst;

import command.ClientCommand;

public class AskChallengeCommand extends ClientCommand {
	public AskChallengeCommand(final Server _server) {
		super(_server, ServerConst.ASK_CHAL);
		addSubcommand(new AskSkirmishChallengeCommand(_server));
		addSubcommand(new AskDragonChallengeCommand(_server));
		addSubcommand(new AskStaghuntChallengeCommand(_server));
	}

	@Override
	protected int routine(final TcpClient _src, final String _cmd,
			final StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}
}
