package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Receiver for the keyboard-input while the server is running
 * 
 * @author Daniel
 */
public class ConsoleInput implements Runnable {
	private final Server _server;
	public volatile boolean _running;

	/**
	 * Constructor
	 * 
	 * @param _server
	 *            instance of the running server
	 */
	public ConsoleInput(Server server) {
		_server = server;
	}

	/**
	 * While the server is active, the {@link ConsoleInput} will attempt to read
	 * input from the console and send it line by line to the server to process
	 * server-sided admin-commands
	 */
	@Override
	public void run() {
		final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String input;
		_running = true;
		try {
			while (_running && (input = console.readLine()) != null) {
				_server.processServerCommand(input);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
