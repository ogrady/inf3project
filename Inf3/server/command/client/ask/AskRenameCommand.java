package command.client.ask;

import java.util.ArrayList;
import java.util.Arrays;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

import environment.wrapper.ServerPlayer;

/**
 * Tries to rename the executing player.<br>
 * Will fail if the player chooses a blacklisted name.
 * @author Daniel
 *
 */
public class AskRenameCommand extends ClientCommand {
	public static final ArrayList<String> blacklist = new ArrayList<String>(Arrays.asList(new String[]{
			"server","admin","administrator"
	})); 

	public AskRenameCommand(Server _server) {
		super(_server, ServerConst.ASK_RENAME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		String oldName = _src.getPlayer().getWrappedObject().getDescription();
		if(_src.getPlayer().authenticated || !blacklist.contains(_cmd.toLowerCase())) {
			_src.getPlayer().getWrappedObject().setDescription(_cmd);
			_src.beginMessage();
			_src.send(ServerConst.ANS+ServerConst.ANS_YES);
			_src.endMessage();
			server.broadcast((ServerPlayer)_src.getPlayer(), ServerConst.UPD);
			_mes.append(String.format("Succesfully renamed %s to %s", oldName, _cmd));
		} else {
			_src.sendNo();
			_mes.append(String.format("Could not rename %s to %s because of name restrictions", oldName, _cmd));
		}
		return 1;
	}

}
