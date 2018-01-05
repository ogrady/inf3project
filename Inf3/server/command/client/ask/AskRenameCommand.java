package command.client.ask;

import java.util.ArrayList;
import java.util.Arrays;

import command.ClientCommand;
import command.Command;
import server.Server;
import server.TcpClient;
import util.ServerConst;

/**
 * Tries to rename the executing player.<br>
 * Will fail if the player chooses a blacklisted name.
 * 
 * @author Daniel
 *
 */
public class AskRenameCommand extends ClientCommand {
	public static final ArrayList<String> blacklist = new ArrayList<>(
			Arrays.asList(new String[] { "server", "admin", "administrator" }));

	public AskRenameCommand(Server server) {
		super(server, ServerConst.ASK_RENAME);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		final String oldName = src.getPlayer().getWrappedObject().getDescription();
		if (src.getPlayer()._authenticated || !blacklist.contains(cmd.toLowerCase())) {
			src.getPlayer().getWrappedObject().setDescription(cmd);
			src.sendOk();
			_server.broadcast(src.getPlayer().getWrappedObject(), ServerConst.UPD);
			mes.append(String.format("Succesfully renamed %s to %s", oldName, cmd));
		} else {
			src.sendNo();
			mes.append(String.format("Could not rename %s to %s because of name restrictions", oldName, cmd));
		}
		return Command.PROCESSED;
	}

}
