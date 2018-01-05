package arena.skirmish;

import server.Server;
import server.TcpClient;
import arena.Arena;

public class SkirmishArena extends Arena<SkirmishOpponent> {
	// sword > alchemy > magic > sword
	private static final int[][] matrix = {
			// s a m
			{ 0, 1, -1 }, // s
			{ -1, 0, 1 }, // a
			{ 1, -1, 0 } // m
	};

	public SkirmishArena(final Server server, final TcpClient owner, final TcpClient challenged, final int rounds) {
		super(server, owner, challenged, rounds);
	}

	@Override
	protected SkirmishOpponent wrap(final TcpClient cl) {
		return new SkirmishOpponent(cl);
	}

	@Override
	protected boolean prerequisites() {
		// opposing players have to be at the same position
		return _player1.getClient().getPlayer().getWrappedObject().getPosition()
				.equals(_player2.getClient().getPlayer().getWrappedObject().getPosition());
	}

	@Override
	protected int[][] getMatrix() {
		return matrix;
	}
}
