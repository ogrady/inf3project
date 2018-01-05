package arena.skirmish;

import arena.Opponent;
import environment.entity.SkirmishDecision;
import server.TcpClient;

public class SkirmishOpponent extends Opponent<SkirmishDecision> {

	public SkirmishOpponent(TcpClient player) {
		super(player);
	}

	@Override
	protected SkirmishDecision getNewDecision() {
		return _player.getPlayer().getWrappedObject().getSkirmishDecision();
	}
}
