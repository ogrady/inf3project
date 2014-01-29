package arena.skirmish;

import server.TcpClient;
import arena.Opponent;
import environment.entity.SkirmishDecision;

public class SkirmishOpponent extends Opponent<SkirmishDecision> {

	public SkirmishOpponent(TcpClient _player) {
		super(_player);
	}

	@Override
	protected SkirmishDecision getNewDecision() {
		return player.getPlayer().getWrappedObject().getSkirmishDecision();
	}
}
