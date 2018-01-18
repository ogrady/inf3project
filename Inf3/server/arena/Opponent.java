package arena;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import environment.entity.DragonDecision;
import environment.entity.SkirmishDecision;
import environment.entity.StaghuntDecision;
import server.TcpClient;
import tokenizer.ITokenizable;
import util.Const;

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
public abstract class Opponent<D extends Enum<?>> implements ITokenizable {
	@JsonIgnore
	protected TcpClient _player;
	protected D decision;
	protected int pointsLastRound, total;

	@JsonIgnore
	abstract protected D getNewDecision();

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

	/**
	 * <pre>
	 * begin:opponent
	 *   id:NR
	 *   dec:DECISION
	 *   pnts:NR
	 *   tot:NR
	 * end:opponent
	 * </pre>
	 */
	@Override
	public ArrayList<String> tokenize() {
		final ArrayList<String> tokens = new ArrayList<>();
		tokens.add(Const.PAR_BEGIN + Const.PAR_OPPONENT);
		tokens.add(Const.PAR_ID + _player.getPlayer().getWrappedObject().getId());
		tokens.add(Const.PAR_DECISION + getLastDecision());
		tokens.add(Const.PAR_POINTS + getPointsLastRound());
		tokens.add(Const.PAR_TOTAL + getTotalPoints());
		tokens.add(Const.PAR_END + Const.PAR_OPPONENT);
		return tokens;
	}
}
