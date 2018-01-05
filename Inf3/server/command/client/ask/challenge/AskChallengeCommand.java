package command.client.ask.challenge;

import server.Server;
import server.TcpClient;
import util.ServerConst;

import command.ClientCommand;

public class AskChallengeCommand extends ClientCommand {
	public AskChallengeCommand(final Server server) {
		super(server, ServerConst.ASK_CHAL);
		addSubcommand(new AskSkirmishChallengeCommand(server));
		addSubcommand(new AskDragonChallengeCommand(server));
		addSubcommand(new AskStaghuntChallengeCommand(server));
	}

	@Override
	protected int routine(final TcpClient src, final String cmd, final StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}
}
