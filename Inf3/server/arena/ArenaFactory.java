package arena;

import server.TcpClient;
import server.Server;
import util.Challenge;
import arena.skirmish.SkirmishArena;

import command.client.ask.challenge.AskSkirmishChallengeCommand;

/**
 * Helper class for the Ask*ChallengeCommands.<br>
 * They are used to reuse certain code-snippets, like checking prerequisites
 * instead of implementing them in every challenge-command.<br>
 * When a Ask*ChallengeCommand is issued that command can instantiate an
 * anonymous instance of {@link ArenaFactory} and implemented the abstract
 * methods inline to match the specific needs for that challenge.<br>
 * See the abstract methods for more information.
 * 
 * @author Daniel
 */
abstract public class ArenaFactory {

	/**
	 * Those are the minimal prerequisites for a successful challenge:
	 * <ul>
	 * <li>the challenger didn't challenge himself</li>
	 * <li>the challenged player is valid (not null)</li>
	 * <li>the challenger must not be busy</li>
	 * <li>the challenged player must not be busy</li>
	 * <li>both players must stand on the same position</li>
	 * </ul>
	 * Additional prerequisites can be added by overwriting this method and calling
	 * super.{@link #prerequisites(TcpClient, TcpClient)}
	 * 
	 * @param challenger
	 *            client who issued the command
	 * @param opponent
	 *            challenged player
	 * @return true, if the prerequisites for this command are met
	 */
	protected boolean prerequisites(TcpClient challenger, TcpClient opponent) {
		return opponent != null && !challenger.equals(opponent) && !opponent.getPlayer().getWrappedObject().isBusy()
				&& !challenger.getPlayer().getWrappedObject().isBusy() && opponent.getPlayer().getWrappedObject()
						.getPosition().equals(challenger.getPlayer().getWrappedObject().getPosition());
	}

	/**
	 * Creates the desired arena. That means when called by the
	 * {@link AskSkirmishChallengeCommand} the anonymous inner instance should
	 * create a {@link SkirmishArena}, for instance
	 * 
	 * @param challenger
	 *            the challenger
	 * @param opponent
	 *            to challenged player
	 * @return the created arena
	 */
	abstract protected Arena<?> createArena(TcpClient challenger, TcpClient opponent);

	/**
	 * Checks, whether the passed arena is of a certain type.<br>
	 * This method is used to check if the challenger already owns an arena of the
	 * type the challenger wants to invite him to.<br>
	 * That way instead of creating a new arena the challenger will simply join the
	 * existing arena.<br>
	 * That means when called by the {@link AskSkirmishChallengeCommand} the
	 * anonymous inner instance should check whether _arena is an instance of
	 * {@link SkirmishArena}
	 * 
	 * @param arena
	 *            the arena to check
	 * @return true, if the passed arena is of the desired type (SkirmishArena,
	 *         DragonArena, ...) depending on the anonymous instance
	 */
	abstract protected boolean matchingArenaType(Arena<?> arena);

	/**
	 * Constructs a new arena for a certain minigame or makes the challenger simply
	 * enter an existing arena if possible.<br>
	 * 
	 * @param server
	 *            running instance of server
	 * @param opponentId
	 *            id of opponent as string (will try to parse)
	 * @param src
	 *            challenger
	 * @param mes
	 *            message to append errors
	 * @return 1 if success, -1 if failed due to an error (invalid id or
	 *         prerequisites are not met)
	 */
	public int construct(Server server, String opponentId, TcpClient src, StringBuilder mes) {
		int result = -1;
		try {
			TcpClient opponent = server.getClientById(Integer.parseInt(opponentId));
			if (!prerequisites(src, opponent)) {
				src.sendNo();
			} else {
				Arena<?> arena = opponent.getPlayer().getArena();
				// opponent already has an arena and is waiting for us -> just enter the already
				// existing arena
				if (arena != null && matchingArenaType(arena) && arena.getChallenged().getClient().equals(src)) {
					src.getPlayer().setArena(arena);
					// _src.flushTokenizable(new ServerMessage(String.format("you have accepted the
					// challenge from
					// %s",opponent.getPlayer().getWrappedObject().getDescription())));
					// opponent.flushTokenizable(new ServerMessage(String.format("%s has accepted
					// your challenge",_src.getPlayer().getWrappedObject().getDescription())));
					opponent.flushTokenizable(generateChallenge(src.getPlayer().getWrappedObject().getId(), true));
					arena.enter(src);
					// create a new arena and invite the opponent to it
				} else {
					src.getPlayer().setArena(createArena(src, opponent));
					// _src.flushTokenizable(new ServerMessage(String.format("you have challenged
					// %s",opponent.getPlayer().getWrappedObject().getDescription())));
					// opponent.flushTokenizable(new ServerMessage(String.format("%s challenged
					// you",_src.getPlayer().getWrappedObject().getDescription())));
					opponent.flushTokenizable(generateChallenge(src.getPlayer().getWrappedObject().getId(), false));
				}
				src.sendOk();
				result = 1;
			}
		} catch (NumberFormatException nfe) {
			mes.append(String.format("Failed to parse id for a challenger from '%s'", opponentId));
		}
		return result;
	}

	protected abstract Challenge generateChallenge(int _challengerId, boolean _accepted);
}
