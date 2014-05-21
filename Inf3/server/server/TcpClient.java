package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import output.Logger;
import output.Logger.MessageType;
import tokenizer.ITokenizable;
import util.ServerConst;
import environment.MapCell;
import environment.entity.Player;
import environment.wrapper.ServerPlayer;

/**
 * Handles the communication between the {@link Server} and one Client (in and
 * out).<br>
 * Every connection gets an own {@link TcpClient} which are stored within the
 * {@link Server}-object.<br>
 * Reading input from the Client runs in an own thread. As soon as the
 * reading-connection breaks down, the {@link TcpClient} will be closed and
 * removed from the {@link Server} by performing a cleanup (deleting the
 * corresponding {@link ServerPlayer} etc.).
 *
 * @author Daniel
 */
public class TcpClient implements Runnable {
	private final Server server;
	private final Socket socket;
	private final PrintWriter output;
	private final BufferedReader input;
	private final ServerPlayer player;
	private volatile int nextId;
	private volatile int mesId;
	private volatile boolean closed;

	/**
	 * Check, whether the {@link TcpClient} was already closed. No further
	 * message should be sent to a closed {@link TcpClient}
	 *
	 * @return whether the {@link TcpClient} is closed or not
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Checks whether the {@link TcpClient} is still waiting for the end of a
	 * message
	 *
	 * @return true, if the mes-id <> -1
	 */
	public boolean pendingMessage() {
		return mesId >= 0;
	}

	/**
	 * @return the {@link Player} object for the connection
	 */
	public ServerPlayer getPlayer() {
		return player;
	}

	/**
	 * @return {@link Socket} the client is connected through
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Constructor
	 *
	 * @param _server
	 *            {@link Server} this client belongs to
	 * @param _socket
	 *            socket the {@link TcpClient} is connected through
	 * @throws IOException
	 *             if opening the input or output stream didn't work
	 */
	public TcpClient(final Server _server, final Socket _socket)
			throws IOException {
		_server.getLogger().println(
				"Received connection from " + _socket.toString(),
				MessageType.NOTIFICATION);
		server = _server;
		socket = _socket;
		output = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
		input = new BufferedReader(new InputStreamReader(new DataInputStream(
				socket.getInputStream())));
		final MapCell randomCell = _server.getMap().getWrappedObject()
				.getRandomWalkableCell();
		player = new ServerPlayer(new Player(randomCell.getX(),
				randomCell.getY(), ""), server);
		mesId = -1;
		server.addClient(this);
		server.ready(this);
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = input.readLine()) != null) {
				server.getLogger().println(
						socket.getInetAddress() + " sent " + line,
						MessageType.INPUT);
				server.processClientCommand(this, line);
			}
		} catch (final IOException e) {
			server.getLogger().println(
					String.format("Client disconnected unexpectedly: '%s'",
							e.getMessage()), MessageType.ERROR);
		} finally {
			close();
		}
	}

	/**
	 * Convenience method to send OK for the last request
	 */
	public synchronized void sendOk() {
		beginMessage();
		send(ServerConst.ANS + ServerConst.ANS_YES);
		endMessage();
	}

	/**
	 * Convenience method to send NO for the last request
	 */
	public synchronized void sendNo() {
		beginMessage();
		send(ServerConst.ANS + ServerConst.ANS_NO);
		endMessage();
	}

	/**
	 * Convenience method to UNKNOWN for the last request. Sends the request
	 * back to the client and marks it as "unknown" to signalize malformed
	 * requests
	 *
	 * @param _req
	 *            original request
	 */
	public synchronized void sendUnknown(final String _req) {
		beginMessage();
		send(ServerConst.ANS + ServerConst.ANS_UNKNOWN + _req);
		endMessage();
	}

	/**
	 * Convenience method to send INVALID for the last request
	 */
	public synchronized void sendInvalid() {
		beginMessage();
		send(ServerConst.ANS + ServerConst.ANS_INVALID);
		endMessage();
	}

	/**
	 * Send a line of text directly to the Client (unbuffered)
	 *
	 * @param _mes
	 *            message to send
	 */
	public synchronized void send(final String _mes) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		if (!closed) {
			output.write(_mes + "\r\n");
			output.flush();
		}
		// }
		// }).run();
	}

	/**
	 * Sends an {@link ITokenizable} by sending its tokenized version line by
	 * line
	 *
	 * @param _tok
	 *            {@link ITokenizable} to send
	 */
	public synchronized void sendTokenizable(final ITokenizable _tok) {
		for (final String t : _tok.tokenize()) {
			send(t);
		}
	}

	/**
	 * Starts the next message. Makes the {@link TcpClient} send the next
	 * available id and remember that id. Each message should then be ended with
	 * a call of {@link #endMessage()}
	 */
	public synchronized void beginMessage() {
		if (mesId == -1) {
			mesId = nextId++;
			send(ServerConst.BEGIN + mesId);
		}
	}

	/**
	 * Ends the message by sending the closing tag with the id of the current
	 * request, as determined by {@link #beginMessage()}. Invalidates the id by
	 * setting it to -1 (= no message pending)
	 */
	public synchronized void endMessage() {
		send(ServerConst.END + this.mesId);
		mesId = -1;
	}

	/**
	 * Directly sends a {@link ITokenizable} with enclosing begin and end
	 *
	 * @param _tok
	 *            {@link ITokenizable} to send
	 */
	public synchronized void flushTokenizable(final ITokenizable _tok) {
		beginMessage();
		sendTokenizable(_tok);
		endMessage();
	}

	/**
	 * Closes the connection (and all streams) to the Client and deletes self
	 * from list of {@link TcpClient}s in the {@link Server}.
	 */
	public synchronized void close() {
		if (!closed) {
			try {
				closed = true;
				server.getLogger().println(
						"Closing connection to " + this.socket.toString(),
						MessageType.NOTIFICATION);
				server.removeClient(this);
				server.terminate(this);
				player.destruct();
				output.close();
				input.close();
				socket.close();
			} catch (final IOException e) {
				closed = false;
				final Logger log = server.getLogger();
				log.println("Error while closing " + this, MessageType.ERROR);
				log.printException(e);
			}
		}
	}

	@Override
	public String toString() {
		return socket.getInetAddress() + ":" + this.socket.getPort();
	}
}
