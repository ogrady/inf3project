package command.client.ask;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import server.TcpClient;
import server.Server;
import util.Configuration;
import util.SHA1;
import util.ServerConst;

import command.ClientCommand;

/**
 * Tries to rename the executing player.<br>
 * Will fail if the player chooses a blacklisted name.
 * @author Daniel
 *
 */
public class AskAuthCommand extends ClientCommand {
	public static final ArrayList<String> blacklist = new ArrayList<String>(Arrays.asList(new String[]{
			"server","admin","administrator"
	})); 

	public AskAuthCommand(Server _server) {
		super(_server, ServerConst.ASK_AUTH);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int result = 1;
		try {
			if(_cmd.equals(new SHA1().hash(Configuration.getInstance().getProperty(Configuration.ADMIN_PASSWORD)))) {
				_src.getPlayer().authenticated = true;
				_mes.append(String.format("%s succesfully authenticated himself", _src.getPlayer().getWrappedObject().getDescription()));
			} else {
				_mes.append(String.format("%s failed to authenticated himself", _src.getPlayer().getWrappedObject().getDescription()));
			}
		} catch (NoSuchAlgorithmException e) {
			server.getLogger().println("Error when hashing with SHA1: algorithm not available on this system");
			result = -1;
		} catch (UnsupportedEncodingException e) {
			server.getLogger().println("Error when hashing with SHA1: UTF-8 encoding not available on this system");
			result = -1;
		}
		return result;
	}

}
