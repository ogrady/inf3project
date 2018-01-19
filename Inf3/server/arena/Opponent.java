package arena;

import com.fasterxml.jackson.annotation.JsonIgnore;

import environment.entity.DragonDecision;
import environment.entity.SkirmishDecision;
import environment.entity.StaghuntDecision;
import server.TcpClient;

/**
 * An {@link Opponent} is a participant in a minigame. It wraps around a
 * {@link TcpClient} to pair with the player.
 * 
 * @author Daniel
 * 
 * @param <D>
 *            {@link SkirmishDecision}, {@link DragonDecision} or
 *            {@link StaghuntDecision}
 */
public abstract class Opponent<D extends Enum<?>> {
	@JsonIgnore
	protected TcpClient _player;
	protected D decision;
	protected int pointsLastRound, total;
	

	@JsonIgnore
	abstract protected D getNewDecision();

	public int getPlayerId() {
		return _player.getPlayer().getWrappedObject().getId();
	}
	
	public D getLastDecision() {
		return decision;
	}

	@JsonIgnore
	public TcpClient getClient() {
		return _player;
	}

	public int getPointsLastRound() {
		return pointsLastRound;
	}

	public int getTotalPoints() {
		return total;
	}

	/**
	 * Constructor
	 * 
	 * @param player
	 *            player to wrap
	 */
	public Opponent(final TcpClient player) {
		_player = player;
		decide();
	}

	/**
	 * Adds a certain amount of points to the total score
	 * 
	 * @param add
	 *            amount
	 */
	public void addPoints(final int add) {
		pointsLastRound = add;
		total += add;
	}

	/**
	 * Checks the current decision of the wrapped player and stores it as result for
	 * {@link #getLastDecision()}
	 */
	public void decide() {
		decision = getNewDecision();
	}
}
