package arena.staghunt;

import server.Server;
import server.TcpClient;
import arena.Arena;
import environment.Property;
import environment.entity.Player;

public class StaghuntArena extends Arena<StaghuntOpponent> {
	private static final int[][] matrix = {
			// s b
			{ 2, -1 }, // s
			{ 1, 0 }, // b
	};

	public StaghuntArena(final Server server, final TcpClient owner, final TcpClient challenged, final int rounds) {
		super(server, owner, challenged, rounds);
	}

	@Override
	public void destruct() {
		final Player p1 = _player1.getClient().getPlayer().getWrappedObject();
		_server.getMap().setHuntableAt(p1.getPosition().x, p1.getPosition().y, false);
		super.destruct();
	}

	@Override
	protected StaghuntOpponent wrap(final TcpClient cl) {
		return new StaghuntOpponent(cl);
	}

	@Override
	protected boolean prerequisites() {
		// opposing players have to be at the same position
		final Player p1 = _player1.getClient().getPlayer().getWrappedObject();
		final Player p2 = _player2.getClient().getPlayer().getWrappedObject();
		return p1.getPosition().equals(p2.getPosition()) && _server.getMap().getWrappedObject()
				.getCellAt(p1.getPosition().x, p1.getPosition().y).hasProperty(Property.HUNTABLE);
	}

	@Override
	protected int[][] getMatrix() {
		return matrix;
	}
}
