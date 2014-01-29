package command.client.ask.challenge;

import arena.Arena;
import arena.ArenaFactory;
import arena.skirmish.SkirmishArena;
import server.TcpClient;
import server.Server;
import util.Challenge;
import util.Configuration;
import util.Const;
import util.ServerConst;
import command.ClientCommand;

public class AskSkirmishChallengeCommand extends ClientCommand {

	public AskSkirmishChallengeCommand(Server _server) {
		super(_server, ServerConst.CHAL_SKIRMISH);
	}

	@Override
	protected int routine(final TcpClient _src, String _cmd, StringBuilder _mes) {
		ArenaFactory factory = new ArenaFactory() {
			@Override
			protected boolean matchingArenaType(Arena<?> _arena) {
				return _arena instanceof SkirmishArena;
			}
			@Override
			protected Arena<?> createArena(TcpClient _challenger, TcpClient _opponent) {
				return new SkirmishArena(server, _challenger, _opponent, Configuration.getInstance().getInteger(Configuration.SKIRMISH_ROUNDS));
			}
			@Override
			protected Challenge generateChallenge(int _challengerId, boolean _accepted) {
				return new Challenge(_src.getPlayer().getWrappedObject().getId(), Const.PAR_TYPE_SKIRMISH, _accepted);
			}
		};
		return factory.construct(server, _cmd, _src, _mes);
	}

	/*@Override
	protected int routine(Client _src, String _cmd, StringBuilder _mes) {
		int result = 0;
		try {
			int id = Integer.parseInt(_cmd);
			Client opponent = server.getClientById(id);
			if(	opponent == null || 
				opponent.getPlayer().getWrappedObject().isBusy() || 
				!opponent.getPlayer().getWrappedObject().getPosition().equals(_src.getPlayer().getWrappedObject().getPosition())) {
				_src.sendNo();
			} else {
				Arena<?> arena = opponent.getPlayer().getArena();
				// opponent already has an arena and is waiting for us -> just enter the already existing arena
				if(arena != null && arena instanceof SkirmishArena && arena.getChallenged().getClient().equals(_src)) {
					_src.getPlayer().setArena(arena);
					arena.enter(_src);
				// create a new arena and invite the opponent to it
				} else {
					_src.getPlayer().setArena(new SkirmishArena(_src, opponent, Configuration.getInstance().getInteger(Configuration.SKIRMISH_ROUNDS)));
				}
				_src.sendOk();
			}
			result = 1;
		} catch(NumberFormatException nfe) {
			result = -1;
			_mes.append(String.format("Failed to parse id for a challenger from '%s'", _cmd));
		}
		return result;
	}*/

}
