package arena.dragonfight;

import server.TcpClient;
import server.Server;
import arena.Arena;
import environment.wrapper.ServerDragon;

public class DragonArena extends Arena<DragonOpponent> {
	private static final int[][] matrix = {{},{}};
	private ServerDragon dragon;
	
	public DragonArena(Server _server, TcpClient _cl1, TcpClient _cl2, ServerDragon _dr, int _rounds) {
		super(_server, _cl1, _cl2, _rounds);
		dragon = _dr;
		dragon.setBusy(true);
	}
	
	@Override
	protected DragonOpponent wrap(TcpClient _cl) {
		return new DragonOpponent(_cl);
	}

	@Override
	protected boolean prerequisites() {
		// all three entities have to be at the same position and the dragon must not be busy
		return !dragon.getWrappedObject().isBusy() &&
				player1.getClient().getPlayer().getWrappedObject().getPosition().equals(player2.getClient().getPlayer().getWrappedObject().getPosition()) &&
				player1.getClient().getPlayer().getWrappedObject().getPosition().equals(dragon.getWrappedObject().getPosition());
	}

	@Override
	protected int[][] getMatrix() {
		return matrix;
	}
}
