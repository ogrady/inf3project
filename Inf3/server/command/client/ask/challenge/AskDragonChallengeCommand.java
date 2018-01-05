package command.client.ask.challenge;

import java.util.Iterator;

import arena.Arena;
import arena.ArenaFactory;
import arena.dragonfight.DragonArena;
import command.ClientCommand;
import environment.wrapper.ServerDragon;
import server.Server;
import server.TcpClient;
import util.Challenge;
import util.Configuration;
import util.Const;
import util.ServerConst;

public class AskDragonChallengeCommand extends ClientCommand {

	public AskDragonChallengeCommand(final Server _server) {
		super(_server, ServerConst.CHAL_DRAGON);
	}

	@Override
	protected int routine(final TcpClient src, final String cmd, final StringBuilder mes) {
		final ArenaFactory factory = new ArenaFactory() {
			@Override
			protected boolean matchingArenaType(final Arena<?> arena) {
				return arena instanceof DragonArena;
			}

			@Override
			protected Arena<?> createArena(final TcpClient challenger, final TcpClient opponent) {
				// TODO: implement some quadtree or let the cells remember their
				// dragons to avoid searching through all dragons every time a
				// challenge is sent. Also, be careful around null-values. Can
				// this case even occur? Gawd, this code sucks hairy monkey
				// balls.
				ServerDragon d = null;
				final Iterator<ServerDragon> it = ServerDragon.instances.values().iterator();
				while (d == null && it.hasNext()) {
					final ServerDragon next = it.next();
					if (!next.getWrappedObject().isBusy() && next.getWrappedObject().getPosition()
							.equals(challenger.getPlayer().getWrappedObject().getPosition())) {
						d = next;
					}
				}
				if (d == null) {

				}
				return new DragonArena(_server, challenger, opponent, d,
						Configuration.getInstance().getInteger(Configuration.DRAGON_ROUNDS));
			}

			@Override
			protected Challenge generateChallenge(final int _challengerId, final boolean _accepted) {
				return new Challenge(src.getPlayer().getWrappedObject().getId(), Const.PAR_TYPE_DRAGON, _accepted);
			}
		};
		return factory.construct(_server, cmd, src, mes);
	}
}
