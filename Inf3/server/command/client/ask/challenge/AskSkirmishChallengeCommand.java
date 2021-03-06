package command.client.ask.challenge;

import arena.Arena;
import arena.ArenaFactory;
import arena.skirmish.SkirmishArena;
import command.ClientCommand;
import server.Server;
import server.TcpClient;
import util.Challenge;
import util.Configuration;
import util.Const;
import util.ServerConst;

public class AskSkirmishChallengeCommand extends ClientCommand {

	public AskSkirmishChallengeCommand(final Server _server) {
		super(_server, ServerConst.CHAL_SKIRMISH);
	}

	@Override
	protected int routine(final TcpClient src, final String cmd, final StringBuilder mes) {
		final ArenaFactory factory = new ArenaFactory() {
			@Override
			protected boolean matchingArenaType(final Arena<?> _arena) {
				return _arena instanceof SkirmishArena;
			}

			@Override
			protected Arena<?> createArena(final TcpClient _challenger, final TcpClient _opponent) {
				return new SkirmishArena(_server, _challenger, _opponent,
						Configuration.getInstance().getInteger(Configuration.SKIRMISH_ROUNDS));
			}

			@Override
			protected Challenge generateChallenge(final int _challengerId, final boolean accepted) {
				return new Challenge(src.getPlayer().getWrappedObject().getId(), Const.PAR_TYPE_SKIRMISH, accepted);
			}
		};
		return factory.construct(_server, cmd, src, mes);
	}
}
