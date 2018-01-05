package arena.staghunt;

import arena.Opponent;
import environment.entity.StaghuntDecision;
import server.TcpClient;

public class StaghuntOpponent extends Opponent<StaghuntDecision> {

	public StaghuntOpponent(final TcpClient player) {
		super(player);
	}

	@Override
	protected StaghuntDecision getNewDecision() {
		return _player.getPlayer().getWrappedObject().getStaghuntDecision();
	}
}
