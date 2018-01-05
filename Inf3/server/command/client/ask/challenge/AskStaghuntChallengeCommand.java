package command.client.ask.challenge;

import arena.Arena;
import arena.ArenaFactory;
import arena.staghunt.StaghuntArena;
import command.ClientCommand;
import server.Server;
import server.TcpClient;
import util.Challenge;
import util.Configuration;
import util.Const;
import util.ServerConst;

public class AskStaghuntChallengeCommand extends ClientCommand {

	public AskStaghuntChallengeCommand(final Server _server) {
		super(_server, ServerConst.CHAL_STAGHUNT);
	}

	@Override
	protected int routine(final TcpClient src, final String cmd, final StringBuilder mes) {
		final ArenaFactory factory = new ArenaFactory() {
			@Override
			protected boolean matchingArenaType(final Arena<?> arena) {
				return arena instanceof StaghuntArena;
			}

			@Override
			protected Arena<?> createArena(final TcpClient challenger, final TcpClient opponent) {
				return new StaghuntArena(_server, challenger, opponent,
						Configuration.getInstance().getInteger(Configuration.STAGHUNT_ROUNDS));
			}

			@Override
			protected Challenge generateChallenge(final int challengerId, final boolean accepted) {
				return new Challenge(src.getPlayer().getWrappedObject().getId(), Const.PAR_TYPE_STAGHUNT, accepted);
			}
		};
		return factory.construct(_server, cmd, src, mes);
	}
}
