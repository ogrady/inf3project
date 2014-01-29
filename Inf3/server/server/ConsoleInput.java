package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Receiver for the keyboard-input while the server is running
 * @author Daniel
 */
public class ConsoleInput implements Runnable {
	private Server server;
	public volatile boolean running;

	/**
	 * Constructor
	 * @param _server instance of the running server
	 */
	public ConsoleInput(Server _server) {
		server = _server;
	}
	
	/**
	 * While the server is active, the {@link ConsoleInput} will attempt to read input from the console
	 * and send it line by line to the server to process server-sided admin-commands
	 */
	@Override
	public void run() {
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String input;
		running = true;
		try {
			while(running && (input = console.readLine()) != null) {
				server.processServerCommand(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
