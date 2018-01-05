package arena.dragonfight;

import server.Server;
import server.TcpClient;
import arena.Arena;
import environment.entity.Player;
import environment.wrapper.ServerDragon;

// TODO: idle arenas can keep dragons busy forever without despawning them. find a mechanism around that
public class DragonArena extends Arena<DragonOpponent> {
	private static final int[][] matrix = {
			// r f
			{ 0, 5 }, // r
			{ 1, 2 }, // f
	};
	private final ServerDragon dragon;

	public DragonArena(final Server server, final TcpClient cl1, final TcpClient cl2, final ServerDragon dr,
			final int _rounds) {
		super(server, cl1, cl2, _rounds);
		dragon = dr;
		if (dragon != null) {
			dragon.setBusy(true);
		}
	}

	@Override
	public void destruct() {
		super.destruct();
		if (dragon != null) {
			dragon.setBusy(false);
		}
	}

	@Override
	protected DragonOpponent wrap(final TcpClient cl) {
		return new DragonOpponent(cl);
	}

	@Override
	protected boolean prerequisites() {
		// all three entities have to be at the same position the players must
		// not be busy and the dragon can not be null. A null-dragon can occur,
		// when no un-busy dragon was found on the cell, the first player
		// challenged the second player on.
		final Player p1 = _player1.getClient().getPlayer().getWrappedObject();
		final Player p2 = _player2.getClient().getPlayer().getWrappedObject();
		return !p1.isBusy() && !p2.isBusy() && dragon != null && p1.getPosition().equals(p2.getPosition())
				&& p1.getPosition().equals(dragon.getWrappedObject().getPosition());
	}

	@Override
	protected int[][] getMatrix() {
		return matrix;
	}
}
