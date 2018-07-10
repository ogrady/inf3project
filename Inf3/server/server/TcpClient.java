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
import util.Answer;
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
	
	private synchronized HashMap<String, HashMap<String, String>> createAnswer(String message, int code) {
		HashMap<String, HashMap<String,String>> response = new HashMap<>();
		HashMap<String,String> inner = new HashMap<>();
		inner.put(ServerConst.ANS_MES, message);
		inner.put(ServerConst.ANS_CODE, ""+code);
		response.put(ServerConst.ANS, inner);
		return response;		
	}

	/**
	 * Convenience method to send OK for the last request
	 */
	public synchronized void sendOk() {
		final Map<String, Answer> mes = new HashMap<>();
		mes.put(ServerConst.ANS, Answer.OK);
		send(_server.json(createAnswer(ServerConst.ANS_YES, ServerConst.CODE_ANS_YES)).get());
	}

	/**
	 * Convenience method to send NO for the last request
	 */
	public synchronized void sendNo() {
		send(_server.json(createAnswer(ServerConst.ANS_NO, ServerConst.CODE_ANS_NO)).get());
	}
	
	/**
	 * Convenience method to UNKNOWN for the last request. Sends the request back to
	 * the client and marks it as "unknown" to signalize malformed requests
	 *
	 * @param req
	 *            original request
	 */
	public synchronized void sendUnknown(final String req) {
		send(_server.json(createAnswer(ServerConst.ANS_UNKNOWN, ServerConst.CODE_ANS_UNKNOWN)).get());
	}

	/**
	 * Convenience method to send INVALID for the last request
	 */
	public synchronized void sendInvalid() {
		final Map<String, String> mes = new HashMap<>();
		mes.put(ServerConst.ANS, ServerConst.ANS_INVALID);
		send(_server.json(createAnswer(ServerConst.ANS_INVALID, ServerConst.CODE_ANS_INVALID)).get());
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
