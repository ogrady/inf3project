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

	public SkirmishArena(final Server _server, final TcpClient _owner,
			final TcpClient _challenged, final int _rounds) {
		super(_server, _owner, _challenged, _rounds);
	}

	@Override
	protected SkirmishOpponent wrap(final TcpClient _cl) {
		return new SkirmishOpponent(_cl);
	}

	@Override
	protected boolean prerequisites() {
		// opposing players have to be at the same position
		return player1
				.getClient()
				.getPlayer()
				.getWrappedObject()
				.getPosition()
				.equals(player2.getClient().getPlayer().getWrappedObject()
						.getPosition());
	}

	@Override
	protected int[][] getMatrix() {
		return matrix;
	}
}
