package arena.staghunt;

import server.TcpClient;
import arena.Opponent;
import environment.entity.StaghuntDecision;

public class StaghuntOpponent extends Opponent<StaghuntDecision> {

	public StaghuntOpponent(final TcpClient _player) {
		super(_player);
	}

	@Override
	protected StaghuntDecision getNewDecision() {
		return player.getPlayer().getWrappedObject().getStaghuntDecision();
	}
}
