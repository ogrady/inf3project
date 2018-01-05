package arena;

import java.util.ArrayList;

import arena.dragonfight.DragonArena;
import arena.dragonfight.DragonOpponent;
import environment.wrapper.ServerPlayer;
import listener.IPlayerListener;
import server.Server;
import server.TcpClient;
import tokenizer.ITokenizable;
import util.Configuration;
import util.Const;
import util.ServerConst;
import util.ServerMessage;

/**
 * An {@link Arena} for minigames. Each {@link Arena} can hold up to two players
 * where player1 is the challenger (A) and player2 is the challenged player (B).
 * When A challenges B he creates a new {@link Arena} and sets himself as
 * player1. He then waits until B accepts the challenge and enters as player2.
 * The round will then start immediately after both players are set and run
 * until the appointed roundnumber as specified in the constructor. Between each
 * round a certain amount of time will pass to give the opponents the chance to
 * reconsider their decisions for the minigame and then check for the points
 * each player receives for that round.<br>
 * The updated players will be broadcasted after the minigame has ended to spare
 * the network from updating the point count every round. The {@link Arena} runs
 * in an own thread and will terminate after enough rounds are played. Both
 * players will be busy for the duration of the game.
 *
 * @author Daniel
 *
 * @param <T>
 *            {@link Opponent}s that can compete in this {@link Arena}
 */
public abstract class Arena<T extends Opponent<?>> implements Runnable, ITokenizable, IPlayerListener {
	protected T _player1, _player2;
	protected int _rounds, _curRound;
	protected boolean _running;
	protected boolean _destructed;
	protected Thread _thread;
	protected Server _server;

	public T getChallenger() {
		return _player1;
	}

	public T getChallenged() {
		return _player2;
	}

	/**
	 * A {@link Arena} can be in waiting state if the challenged player hasn't
	 * accepted or denied the challenge yet. As soon as both players accepted, the
	 * minigame will start and the {@link Arena} will be put into the running
	 * state.<br>
	 *
	 * @return true, if both players accepted the challenge and the arena is active
	 *         and we haven't reached the max. round-number yet
	 */
	public boolean isRunning() {
		return _running && _curRound < _rounds;
	}

	/**
	 * Constructor creates an {@link Arena} that plays up to "_rounds" rounds
	 *
	 * @param owner
	 *            player that created the {@link Arena} by challenging another
	 *            player
	 * @param challenged
	 *            player that was challenged and yet has to accept
	 * @param _rounds
	 *            rounds to play until the minigame ended
	 */
	public Arena(final Server server, final TcpClient owner, final TcpClient challenged, final int rounds) {
		_server = server;
		_player1 = wrap(owner);
		_player2 = wrap(challenged);
		_rounds = rounds;
	}

	/**
	 * Destructor can be overridden to do some cleanup after the minigame has ended.
	 * Will be called before the thread terminates.
	 */
	protected void destruct() {
		_running = false;
		_player1.getClient().getPlayer().addPoints(_player1.getTotalPoints());
		_player2.getClient().getPlayer().addPoints(_player2.getTotalPoints());
		_player1.getClient().getPlayer().setBusy(false);
		_player2.getClient().getPlayer().setBusy(false);
		_player1.getClient().getPlayer().getListeners().unregisterListener(this);
		_player2.getClient().getPlayer().getListeners().unregisterListener(this);
		_player1.getClient().flushTokenizable(new ServerMessage("game has ended"));
		_player2.getClient().flushTokenizable(new ServerMessage("game has ended"));
		_player1.getClient().getPlayer().setArena(null);
		_player2.getClient().getPlayer().setArena(null);
		_player1 = null;
		_player2 = null;
	}

	/**
	 * Make an attempt to enter the {@link Arena}. This will only succeed if the
	 * entering player is the challenged player (player2). Entering the
	 * {@link Arena} will immediately start the minigame by calling {@link #run()}
	 * with a wrapping thread.
	 *
	 * @param cl
	 * @return true, if entering succeeded, false if not (player was not invited)
	 */
	public boolean enter(final TcpClient cl) {
		boolean entered = false;
		if (cl.equals(_player2.getClient())) {
			new Thread(this,
					String.format("arena (%d vs %d)", _player1.getClient().getPlayer().getWrappedObject().getId(),
							_player2.getClient().getPlayer().getWrappedObject().getId())).start();
			entered = true;
		}
		return entered;
	}

	@Override
	public void run() {
		_running = true;
		if (prerequisites()) {
			_player1.getClient().getPlayer().getListeners().registerListener(this);
			_player2.getClient().getPlayer().getListeners().registerListener(this);
			_player1.getClient().getPlayer().setBusy(true);
			_player2.getClient().getPlayer().setBusy(true);
			// while (running && curRound < rounds) {
			while (isRunning()) {
				try {
					Thread.sleep(Configuration.getInstance().getLong(Configuration.MINIGAME_ROUND_DELAY));
					doRound();
					sendResult();
				} catch (final Exception e) {
					e.printStackTrace();
				}
				_curRound++;
			}
		}
		destruct();
	}

	/**
	 * Sends the result of one round to both players (see {@link #tokenize()}).
	 */
	protected void sendResult() {
		_player1.getClient().flushTokenizable(this);
		_player2.getClient().flushTokenizable(this);
		_player1.getClient().flushTokenizable(new ServerMessage(String
				.format("At the end of round %d you have a total of %d points", _curRound, _player1.getTotalPoints())));
		_player2.getClient().flushTokenizable(new ServerMessage(String
				.format("At the end of round %d you have a total of %d points", _curRound, _player2.getTotalPoints())));
	}

	/**
	 * Plays one round by getting the latest decision of both players and checking
	 * the pairing of the decisions of the opponents. Then adds the points each
	 * player earned to their score.
	 */
	protected void doRound() {
		_player1.decide();
		_player2.decide();
		_player1.addPoints(getPoints(_player1, _player2));
		_player2.addPoints(getPoints(_player2, _player1));
	}

	/**
	 * <pre>
	 * begin:result
	 *   rnd:NR
	 *   running:BOOL
	 *   delay:NR
	 *   begin:players
	 *     {@link Opponent#tokenize()}
	 *     {@link Opponent#tokenize()}
	 *   end:players
	 * end:result
	 * </pre>
	 */
	@Override
	public ArrayList<String> tokenize() {
		final ArrayList<String> tokens = new ArrayList<>();
		tokens.add(ServerConst.BEGIN + Const.PAR_RESULT);
		tokens.add(Const.PAR_ROUND + _curRound);
		// -1 to do "foresight" whether this is the last round =
		// "will the game be running for another round after this round?"
		tokens.add(Const.PAR_RUNNING + (_running && _curRound < _rounds - 1));
		tokens.add(Const.PAR_DELAY + Configuration.getInstance().getInteger(Configuration.MINIGAME_ROUND_DELAY));
		tokens.add(ServerConst.BEGIN + Const.PAR_OPPONENTS);
		tokens.addAll(_player1.tokenize());
		tokens.addAll(_player2.tokenize());
		tokens.add(ServerConst.END + Const.PAR_OPPONENTS);
		tokens.add(ServerConst.END + Const.PAR_RESULT);
		return tokens;
	}

	@Override
	public void onDisconnect(final ServerPlayer player) {
		// this makes the minigame call destruct() after waking up the next time
		_running = false;
	}

	protected T getOpponent(final T self) {
		T opponent = null;
		if (self.equals(_player1)) {
			opponent = _player2;
		} else if (self.equals(_player2)) {
			opponent = _player1;
		}
		return opponent;
	}

	/**
	 * Calculates the points for the last round by looking it up in the specific
	 * matrix.<br>
	 * The keys for the lookup are the ordinal numbers of the decision of the two
	 * players in the enum. So the order of those is sensitive!
	 *
	 * @param p1Dec
	 *            player1s wrapper from which the decision is taken
	 * @param p2Dec
	 *            player2s wrapper from which the decision is taken
	 * @return the points for player passed as first argument
	 */
	// abstract protected int getPoints(T _p1Dec, T _p2Dec);
	private int getPoints(final T p1Dec, final T p2Dec) {
		final int[][] matrix = getMatrix();
		return matrix[p1Dec.getNewDecision().ordinal()][p2Dec.getNewDecision().ordinal()];
	}

	/**
	 * Processes a {@link TcpClient} by wrapping an arena-specific player around it.
	 * For example in a {@link DragonArena} the {@link TcpClient}s will be wrapped
	 * into {@link DragonOpponent}s. Their type is specified by the generic T.
	 *
	 * @param cl
	 *            {@link TcpClient} that should be wrapped
	 * @return wrapped version of the {@link TcpClient}
	 */
	abstract protected T wrap(TcpClient cl);

	/**
	 * Checks whether the prerequisites for starting the game are met. Otherwise the
	 * game will be canceled.
	 *
	 * @return true, if all prerequisites are met
	 */
	abstract protected boolean prerequisites();

	/**
	 * @return the matrix in which the points for the minigame are stored.
	 */
	abstract protected int[][] getMatrix();
}
