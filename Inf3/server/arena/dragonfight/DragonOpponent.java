package arena.dragonfight;

import arena.Opponent;
import environment.entity.DragonDecision;
import server.TcpClient;

public class DragonOpponent extends Opponent<DragonDecision> {

	public DragonOpponent(TcpClient player) {
		super(player);
	}

	@Override
	protected DragonDecision getNewDecision() {
		return _player.getPlayer().getWrappedObject().getDragonDecision();
	}

}
