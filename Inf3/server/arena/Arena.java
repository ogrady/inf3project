package arena;

import java.util.ArrayList;

import listener.IPlayerListener;
import server.TcpClient;
import server.Server;
import tokenizer.ITokenizable;
import util.Configuration;
import util.Const;
import util.ServerConst;
import util.ServerMessage;
import arena.dragonfight.DragonArena;
import arena.dragonfight.DragonOpponent;
import environment.wrapper.ServerPlayer;

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
public abstract class Arena<T extends Opponent<?>> implements Runnable,
		ITokenizable, IPlayerListener {
	protected T player1, player2;
	protected int rounds, curRound;
	protected boolean running;
	protected boolean destructed;
	protected Thread thread;
	protected Server server;

	public T getChallenger() {
		return player1;
	}

	public T getChallenged() {
		return player2;
	}

	/**
	 * A {@link Arena} can be in waiting state if the challenged player hasn't
	 * accepted or denied the challenge yet. As soon as both players accepted,
	 * the minigame will start and the {@link Arena} will be put into the
	 * running state.<br>
	 * 
	 * @return true, if both players accepted the challenge and the arena is
	 *         active and we haven't reached the max. round-number yet
	 */
	public boolean isRunning() {
		return running && curRound < rounds;
	}

	/**
	 * Constructor creates an {@link Arena} that plays up to "_rounds" rounds
	 * 
	 * @param _owner
	 *            player that created the {@link Arena} by challenging another
	 *            player
	 * @param _challenged
	 *            player that was challenged and yet has to accept
	 * @param _rounds
	 *            rounds to play until the minigame ended
	 */
	public Arena(final Server _server, final TcpClient _owner,
			final TcpClient _challenged, final int _rounds) {
		server = _server;
		player1 = wrap(_owner);
		player2 = wrap(_challenged);
		rounds = _rounds;
	}

	/**
	 * Destructor can be overridden to do some cleanup after the minigame has
	 * ended. Will be called before the thread terminates.
	 */
	protected void destruct() {
		running = false;
		player1.getClient().getPlayer().addPoints(player1.getTotalPoints());
		player2.getClient().getPlayer().addPoints(player2.getTotalPoints());
		player1.getClient().getPlayer().setBusy(false);
		player2.getClient().getPlayer().setBusy(false);
		player1.getClient().getPlayer().getListeners().unregisterListener(this);
		player2.getClient().getPlayer().getListeners().unregisterListener(this);
		player1.getClient().flushTokenizable(
				new ServerMessage("game has ended"));
		player2.getClient().flushTokenizable(
				new ServerMessage("game has ended"));
		player1.getClient().getPlayer().setArena(null);
		player2.getClient().getPlayer().setArena(null);
		player1 = null;
		player2 = null;
	}

	/**
	 * Make an attempt to enter the {@link Arena}. This will only succeed if the
	 * entering player is the challenged player (player2). Entering the
	 * {@link Arena} will immediately start the minigame by calling
	 * {@link #run()} with a wrapping thread.
	 * 
	 * @param _cl
	 * @return true, if entering succeeded, false if not (player was not
	 *         invited)
	 */
	public boolean enter(final TcpClient _cl) {
		boolean entered = false;
		if (_cl.equals(player2.getClient())) {
			server.getThreadpool().execute(this);
			entered = true;
		}
		return entered;
	}

	@Override
	public void run() {
		running = true;
		if (prerequisites()) {
			player1.getClient().getPlayer().getListeners()
					.registerListener(this);
			player2.getClient().getPlayer().getListeners()
					.registerListener(this);
			player1.getClient().getPlayer().setBusy(true);
			player2.getClient().getPlayer().setBusy(true);
			// while (running && curRound < rounds) {
			while (isRunning()) {
				try {
					Thread.sleep(Configuration.getInstance().getLong(
							Configuration.MINIGAME_ROUND_DELAY));
					doRound();
					sendResult();
				} catch (final Exception e) {
					e.printStackTrace();
				}
				curRound++;
			}
		}
		destruct();
	}

	/**
	 * Sends the result of one round to both players (see {@link #tokenize()}).
	 */
	protected void sendResult() {
		player1.getClient().flushTokenizable(this);
		player2.getClient().flushTokenizable(this);
		player1.getClient().flushTokenizable(
				new ServerMessage(String.format(
						"At the end of round %d you have a total of %d points",
						curRound, player1.getTotalPoints())));
		player2.getClient().flushTokenizable(
				new ServerMessage(String.format(
						"At the end of round %d you have a total of %d points",
						curRound, player2.getTotalPoints())));
	}

	/**
	 * Plays one round by getting the latest decision of both players and
	 * checking the pairing of the decisions of the opponents. Then adds the
	 * points each player earned to their score.
	 */
	protected void doRound() {
		player1.decide();
		player2.decide();
		player1.addPoints(getPoints(player1, player2));
		player2.addPoints(getPoints(player2, player1));
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
		final ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN + Const.PAR_RESULT);
		tokens.add(Const.PAR_ROUND + curRound);
		// -1 to do "foresight" whether this is the last round =
		// "will the game be running for another round after this round?"
		tokens.add(Const.PAR_RUNNING + (running && curRound < rounds - 1));
		tokens.add(Const.PAR_DELAY
				+ Configuration.getInstance().getInteger(
						Configuration.MINIGAME_ROUND_DELAY));
		tokens.add(ServerConst.BEGIN + Const.PAR_OPPONENTS);
		tokens.addAll(player1.tokenize());
		tokens.addAll(player2.tokenize());
		tokens.add(ServerConst.END + Const.PAR_OPPONENTS);
		tokens.add(ServerConst.END + Const.PAR_RESULT);
		return tokens;
	}

	@Override
	public void onDisconnect(final ServerPlayer _player) {
		// this makes the minigame call destruct() after waking up the next time
		running = false;
	}

	protected T getOpponent(final T _self) {
		T opponent = null;
		if (_self.equals(player1)) {
			opponent = player2;
		} else if (_self.equals(player2)) {
			opponent = player1;
		}
		return opponent;
	}

	/**
	 * Calculates the points for the last round by looking it up in the specific
	 * matrix.<br>
	 * The keys for the lookup are the ordinal numbers of the decision of the
	 * two players in the enum. So the order of those is sensitive!
	 * 
	 * @param _p1Dec
	 *            player1s wrapper from which the decision is taken
	 * @param _p2Dec
	 *            player2s wrapper from which the decision is taken
	 * @return the points for player passed as first argument
	 */
	// abstract protected int getPoints(T _p1Dec, T _p2Dec);
	private int getPoints(final T _p1Dec, final T _p2Dec) {
		final int[][] matrix = getMatrix();
		return matrix[_p1Dec.getNewDecision().ordinal()][_p2Dec
				.getNewDecision().ordinal()];
	}

	/**
	 * Processes a {@link TcpClient} by wrapping an arena-specific player around
	 * it. For example in a {@link DragonArena} the {@link TcpClient}s will be
	 * wrapped into {@link DragonOpponent}s. Their type is specified by the
	 * generic T.
	 * 
	 * @param _cl
	 *            {@link TcpClient} that should be wrapped
	 * @return wrapped version of the {@link TcpClient}
	 */
	abstract protected T wrap(TcpClient _cl);

	/**
	 * Checks whether the prerequisites for starting the game are met. Otherwise
	 * the game will be canceled.
	 * 
	 * @return true, if all prerequisites are met
	 */
	abstract protected boolean prerequisites();

	/**
	 * @return the matrix in which the points for the minigame are stored.
	 */
	abstract protected int[][] getMatrix();
}
