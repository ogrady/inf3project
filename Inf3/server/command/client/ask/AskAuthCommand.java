package command.client.ask;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import command.ClientCommand;
import server.Server;
import server.TcpClient;
import util.Configuration;
import util.SHA1;
import util.ServerConst;

/**
 * Tries to rename the executing player.<br>
 * Will fail if the player chooses a blacklisted name.
 * 
 * @author Daniel
 *
 */
public class AskAuthCommand extends ClientCommand {
	public static final ArrayList<String> blacklist = new ArrayList<>(
			Arrays.asList(new String[] { "server", "admin", "administrator" }));

	public AskAuthCommand(Server server) {
		super(server, ServerConst.ASK_AUTH);
	}

	@Override
	protected int routine(TcpClient src, String cmd, StringBuilder mes) {
		int result = 1;
		try {
			if (cmd.equals(new SHA1().hash(Configuration.getInstance().getProperty(Configuration.ADMIN_PASSWORD)))) {
				src.getPlayer()._authenticated = true;
				mes.append(String.format("%s succesfully authenticated himself",
						src.getPlayer().getWrappedObject().getDescription()));
			} else {
				mes.append(String.format("%s failed to authenticated himself",
						src.getPlayer().getWrappedObject().getDescription()));
			}
		} catch (final NoSuchAlgorithmException e) {
			_server.getLogger().println("Error when hashing with SHA1: algorithm not available on this system");
			result = -1;
		} catch (final UnsupportedEncodingException e) {
			_server.getLogger().println("Error when hashing with SHA1: UTF-8 encoding not available on this system");
			result = -1;
		}
		return result;
	}

}
