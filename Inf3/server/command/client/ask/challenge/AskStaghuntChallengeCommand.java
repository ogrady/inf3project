package command.client.ask.challenge;

import server.Server;
import server.TcpClient;
import util.Challenge;
import util.Configuration;
import util.Const;
import util.ServerConst;
import arena.Arena;
import arena.ArenaFactory;
import arena.staghunt.StaghuntArena;

import command.ClientCommand;

public class AskStaghuntChallengeCommand extends ClientCommand {

	public AskStaghuntChallengeCommand(final Server _server) {
		super(_server, ServerConst.CHAL_STAGHUNT);
	}

	@Override
	protected int routine(final TcpClient _src, final String _cmd,
			final StringBuilder _mes) {
		final ArenaFactory factory = new ArenaFactory() {
			@Override
			protected boolean matchingArenaType(final Arena<?> _arena) {
				return _arena instanceof StaghuntArena;
			}

			@Override
			protected Arena<?> createArena(final TcpClient _challenger,
					final TcpClient _opponent) {
				return new StaghuntArena(server, _challenger, _opponent,
						Configuration.getInstance().getInteger(
								Configuration.STAGHUNT_ROUNDS));
			}

			@Override
			protected Challenge generateChallenge(final int _challengerId,
					final boolean _accepted) {
				return new Challenge(_src.getPlayer().getWrappedObject()
						.getId(), Const.PAR_TYPE_STAGHUNT, _accepted);
			}
		};
		return factory.construct(server, _cmd, _src, _mes);
	}
}
