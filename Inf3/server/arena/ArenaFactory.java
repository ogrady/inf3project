package arena;

import server.TcpClient;
import server.Server;
import util.Challenge;
import arena.skirmish.SkirmishArena;

import command.client.ask.challenge.AskSkirmishChallengeCommand;

/**
 * Helper class for the Ask*ChallengeCommands.<br>
 * They are used to reuse certain code-snippets, like checking prerequisites instead of
 * implementing them in every challenge-command.<br>
 * When a Ask*ChallengeCommand is issued that command can instantiate an anonymous instance of {@link ArenaFactory} and
 * implemented the abstract methods inline to match the specific needs for that challenge.<br>
 * See the abstract methods for more information.
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
	 * Additional prerequisites can be added by overwriting this method and calling super.{@link #prerequisites(TcpClient, TcpClient)}
	 * @param _challenger client who issued the command
	 * @param _opponent challenged player
	 * @return true, if the prerequisites for this command are met
	 */
	protected boolean prerequisites(TcpClient _challenger, TcpClient _opponent) {
		return _opponent != null 
				&& !_challenger.equals(_opponent)
				&& !_opponent.getPlayer().getWrappedObject().isBusy() 
				&& !_challenger.getPlayer().getWrappedObject().isBusy()
				&& _opponent.getPlayer().getWrappedObject().getPosition().equals(_challenger.getPlayer().getWrappedObject().getPosition());
	}
	
	/**
	 * Creates the desired arena. That means when called by the {@link AskSkirmishChallengeCommand} the anonymous
	 * inner instance should create a {@link SkirmishArena}, for instance
	 * @param _challenger the challenger
	 * @param _opponent to challenged player
	 * @return the created arena
	 */
	abstract protected Arena<?> createArena(TcpClient _challenger, TcpClient _opponent);
	/**
	 * Checks, whether the passed arena is of a certain type.<br>
	 * This method is used to check if the challenger already owns an arena of the type the challenger wants to invite him to.<br>
	 * That way instead of creating a new arena the challenger will simply join the existing arena.<br>
	 * That  means when called by the {@link AskSkirmishChallengeCommand} the anonymous
	 * inner instance should check whether _arena is an instance of {@link SkirmishArena} 
	 * @param _arena the arena to check
	 * @return true, if the passed arena is of the desired type (SkirmishArena, DragonArena, ...) depending on the anonymous instance
	 */
	abstract protected boolean matchingArenaType(Arena<?> _arena);
	
	/**
	 * Constructs a new arena for a certain minigame or makes the challenger simply enter an existing arena if possible.<br>
	 * 
	 * @param _server running instance of server
	 * @param _opponentId id of opponent as string (will try to parse)
	 * @param _src challenger
	 * @param _mes message to append errors
	 * @return 1 if success, -1 if failed due to an error (invalid id or prerequisites are not met)
	 */
	public int construct(Server _server, String _opponentId, TcpClient _src, StringBuilder _mes) {
		int result = -1;
		try {
			TcpClient opponent = _server.getClientById(Integer.parseInt(_opponentId));
			if(	!prerequisites(_src, opponent)) {
				_src.sendNo();
			} else {
				Arena<?> arena = opponent.getPlayer().getArena();
				// opponent already has an arena and is waiting for us -> just enter the already existing arena
				if(arena != null && matchingArenaType(arena) && arena.getChallenged().getClient().equals(_src)) {
					_src.getPlayer().setArena(arena);
					//_src.flushTokenizable(new ServerMessage(String.format("you have accepted the challenge from %s",opponent.getPlayer().getWrappedObject().getDescription())));
					//opponent.flushTokenizable(new ServerMessage(String.format("%s has accepted your challenge",_src.getPlayer().getWrappedObject().getDescription())));
					opponent.flushTokenizable(generateChallenge(_src.getPlayer().getWrappedObject().getId(), true));
					arena.enter(_src);
				// create a new arena and invite the opponent to it
				} else {
					_src.getPlayer().setArena(createArena(_src, opponent));
					//_src.flushTokenizable(new ServerMessage(String.format("you have challenged %s",opponent.getPlayer().getWrappedObject().getDescription())));
					//opponent.flushTokenizable(new ServerMessage(String.format("%s challenged you",_src.getPlayer().getWrappedObject().getDescription())));
					opponent.flushTokenizable(generateChallenge(_src.getPlayer().getWrappedObject().getId(), false));
				}
				_src.sendOk();
				result = 1;
			}
		} catch(NumberFormatException nfe) {
			_mes.append(String.format("Failed to parse id for a challenger from '%s'", _opponentId));
		}
		return result;
	}
	
	protected abstract Challenge generateChallenge(int _challengerId, boolean _accepted);
}
