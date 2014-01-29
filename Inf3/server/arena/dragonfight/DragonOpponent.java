package arena.dragonfight;

import server.TcpClient;
import arena.Opponent;
import environment.entity.DragonDecision;

public class DragonOpponent extends Opponent<DragonDecision>{

	public DragonOpponent(TcpClient _player) {
		super(_player);
	}

	@Override
	protected DragonDecision getNewDecision() {
		return player.getPlayer().getWrappedObject().getDragonDecision();
	}

}
