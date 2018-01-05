package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import environment.MapCell;
import environment.entity.Player;
import environment.wrapper.ServerPlayer;
import output.Logger;
import output.Logger.MessageType;
import tokenizer.ITokenizable;
import util.ServerConst;

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
	private final Server _server;
	private final Socket _socket;
	private final PrintWriter _output;
	private final BufferedReader _input;
	private final ServerPlayer _player;
	private volatile int _nextId;
	private volatile int _mesId;
	private volatile boolean _closed;

	/**
	 * Check, whether the {@link TcpClient} was already closed. No further message
	 * should be sent to a closed {@link TcpClient}
	 *
	 * @return whether the {@link TcpClient} is closed or not
	 */
	public boolean isClosed() {
		return _closed;
	}

	/**
	 * Checks whether the {@link TcpClient} is still waiting for the end of a
	 * message
	 *
	 * @return true, if the mes-id <> -1
	 */
	public boolean pendingMessage() {
		return _mesId >= 0;
	}

	/**
	 * @return the {@link Player} object for the connection
	 */
	public ServerPlayer getPlayer() {
		return _player;
	}

	/**
	 * @return {@link Socket} the client is connected through
	 */
	public Socket getSocket() {
		return _socket;
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
	public TcpClient(final Server server, final Socket socket) throws IOException {
		server.getLogger().println("Received connection from " + socket.toString(), MessageType.NOTIFICATION);
		_server = server;
		_socket = socket;
		_output = new PrintWriter(new DataOutputStream(_socket.getOutputStream()));
		_input = new BufferedReader(new InputStreamReader(new DataInputStream(_socket.getInputStream())));
		final MapCell randomCell = _server.getMap().getWrappedObject().getRandomWalkableCell();
		_player = new ServerPlayer(new Player(randomCell.getX(), randomCell.getY(), ""), _server);
		_mesId = -1;
		_server.addClient(this);
		_server.ready(this);
	}

	@Override
	public void run() {
		try {
			String line;
			while ((line = _input.readLine()) != null) {
				_server.getLogger().println(_socket.getInetAddress() + " sent " + line, MessageType.INPUT);
				_server.processClientCommand(this, line);
			}
		} catch (final IOException e) {
			_server.getLogger().println(String.format("Client disconnected unexpectedly: '%s'", e.getMessage()),
					MessageType.ERROR);
		} finally {
			close();
		}
	}

	/**
	 * Convenience method to send OK for the last request
	 */
	public synchronized void sendOk() {
		final Map<String, String> mes = new HashMap<>();
		mes.put(ServerConst.ANS, ServerConst.ANS_YES);
		send(_server.json(mes).get());
		/*
		 * beginMessage(); send(ServerConst.ANS + ServerConst.ANS_YES); endMessage();
		 */
	}

	/**
	 * Convenience method to send NO for the last request
	 */
	public synchronized void sendNo() {
		final Map<String, String> mes = new HashMap<>();
		mes.put(ServerConst.ANS, ServerConst.ANS_NO);
		send(_server.json(mes).get());
		/*
		 * beginMessage(); send(ServerConst.ANS + ServerConst.ANS_NO); endMessage();
		 */
	}

	/**
	 * Convenience method to UNKNOWN for the last request. Sends the request back to
	 * the client and marks it as "unknown" to signalize malformed requests
	 *
	 * @param req
	 *            original request
	 */
	public synchronized void sendUnknown(final String req) {
		final Map<String, String> mes = new HashMap<>();
		mes.put(ServerConst.ANS, ServerConst.ANS_UNKNOWN);
		send(_server.json(mes).get());
		/*
		 * beginMessage(); send(ServerConst.ANS + ServerConst.ANS_UNKNOWN + _req);
		 * endMessage();
		 */
	}

	/**
	 * Convenience method to send INVALID for the last request
	 */
	public synchronized void sendInvalid() {
		final Map<String, String> mes = new HashMap<>();
		mes.put(ServerConst.ANS, ServerConst.ANS_INVALID);
		send(_server.json(mes).get());
		/*
		 * beginMessage(); send(ServerConst.ANS + ServerConst.ANS_INVALID);
		 * endMessage();
		 */
	}

	/**
	 * Send a line of text directly to the Client (unbuffered)
	 *
	 * @param mes
	 *            message to send
	 */
	public synchronized void send(final String mes) {
		if (!_closed) {
			_output.write(mes + "\r\n");
			_output.flush();
		}
	}

	/**
	 * Sends an {@link ITokenizable} by sending its tokenized version line by line
	 *
	 * @param tok
	 *            {@link ITokenizable} to send
	 */
	public synchronized void sendTokenizable(final ITokenizable tok) {
		for (final String t : tok.tokenize()) {
			send(t);
		}
	}

	/**
	 * Starts the next message. Makes the {@link TcpClient} send the next available
	 * id and remember that id. Each message should then be ended with a call of
	 * {@link #endMessage()}
	 */
	public synchronized void beginMessage() {
		if (_mesId == -1) {
			_mesId = _nextId++;
			send(ServerConst.BEGIN + _mesId);
		}
	}

	/**
	 * Ends the message by sending the closing tag with the id of the current
	 * request, as determined by {@link #beginMessage()}. Invalidates the id by
	 * setting it to -1 (= no message pending)
	 */
	public synchronized void endMessage() {
		send(ServerConst.END + this._mesId);
		_mesId = -1;
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
	 * Closes the connection (and all streams) to the Client and deletes self from
	 * list of {@link TcpClient}s in the {@link Server}.
	 */
	public synchronized void close() {
		if (!_closed) {
			try {
				_closed = true;
				_server.getLogger().println("Closing connection to " + this._socket.toString(),
						MessageType.NOTIFICATION);
				_server.removeClient(this);
				_server.terminate(this);
				_player.destruct();
				_output.close();
				_input.close();
				_socket.close();
			} catch (final IOException e) {
				_closed = false;
				final Logger log = _server.getLogger();
				log.println("Error while closing " + this, MessageType.ERROR);
				log.printException(e);
			}
		}
	}

	@Override
	public String toString() {
		return _socket.getInetAddress() + ":" + this._socket.getPort();
	}
}
