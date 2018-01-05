package command.server;

import server.Server;
import util.ServerConst;

import command.ServerCommand;

/**
 * Spawns a dragon on a random cell.<br>
 * Always succeeds.
 * 
 * @author Daniel
 */
public class SpawnDragonCommand extends ServerCommand {
	public SpawnDragonCommand() {
		super(ServerConst.SC_SPAWN_DRAGON);
	}

	@Override
	protected int routine(Server src, String cmd, StringBuilder mes) {
		mes.append(String.format("Successfully spawned a dragon on %s", src.getMap().spawnDragonOnRandomCell()));
		return 1;
	}
}
