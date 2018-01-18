package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import command.ClientCommand;
import command.ServerCommand;
import command.TopLevelCommand;
import command.client.ask.AskAuthCommand;
import command.client.ask.AskCommand;
import command.client.ask.AskDisconnectCommand;
import command.client.ask.AskMoveCommand;
import command.client.ask.AskRenameCommand;
import command.client.ask.AskSayCommand;
import command.client.ask.challenge.AskChallengeCommand;
import command.client.ask.set.AskSetCommand;
import command.client.get.GetAllEntitesCommand;
import command.client.get.GetCommand;
import command.client.get.GetEntityCommand;
import command.client.get.GetMapCommand;
import command.client.get.GetOnlineCountCommand;
import command.client.get.GetOwnIDCommand;
import command.client.get.GetRankingCommand;
import command.client.get.GetSelfCommand;
import command.client.get.GetServertimeCommand;
import command.client.get.GetUsersCommand;
import command.server.HelpCommand;
import command.server.InfoCommand;
import command.server.KickCommand;
import command.server.ListCommand;
import command.server.LoggerSensitivityCommand;
import command.server.ReloadCommand;
import command.server.ShutdownCommand;
import command.server.SpawnDragonCommand;
import environment.MapCell;
import environment.ServerClock;
import environment.entity.Dragon;
import environment.entity.Player;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerMap;
import exception.MapException;
import listener.IDragonListener;
import listener.IListenable;
import listener.IMapListener;
import listener.IServerListener;
import listener.ListenerSet;
import output.Logger;
import output.Logger.MessageType;
import output.TimeLogger;
import tokenizer.ITokenizable;
import util.Configuration;
import util.Const;
import util.ServerConst;
import util.ServerMessage;
import util.ServerState;
import util.Vector2D;

/**
 * Server for the game
 *
 * @author Daniel
 */
public class Server implements IListenable<IServerListener>, IMapListener, IDragonListener {
	public static final int serverVersionMajor = 2;
	public static final int serverVersionMinor = 0;
	public static final ServerState serverState = ServerState.EXPERIMENTAL;

	private boolean _running = false;
	private int _port;
	private ObjectMapper _objectMapper;
	private ServerMap _map;
	private ServerSocket _socket;
	private final ServerClock _clock = new ServerClock(this);
	private final Logger _logger = new TimeLogger();
	private final ListenerSet<IServerListener> _listeners = new ListenerSet<>();;
	private final ConsoleInput _consoleInput = new ConsoleInput(this);
	private final CopyOnWriteArrayList<TcpClient> _clients = new CopyOnWriteArrayList<>();
	private final TopLevelCommand<Server> _serverCommands = new TopLevelCommand<>();
	private final TopLevelCommand<TcpClient> _clientCommands = new TopLevelCommand<>();

	/**
	 * @return {@link ObjectMapper} used to JSON-serialise objects
	 */
	public ObjectMapper getObjectMapper() {
		return _objectMapper;
	}

	/**
	 * @return {@link Logger} that is currently in use
	 */
	public Logger getLogger() {
		return _logger;
	}

	/**
	 * @return the {@link ServerMap} the server is using
	 */
	public ServerMap getMap() {
		return _map;
	}

	/**
	 * @return a {@link CopyOnWriteArrayList} with {@link TcpClient}s that are
	 *         currently running
	 */
	public CopyOnWriteArrayList<TcpClient> getClients() {
		return _clients;
	}

	/**
	 * Searches a {@link TcpClient} by the ID of its contained entity
	 *
	 * @param _id
	 *            id of the {@link TcpClient} to search for
	 * @return the {@link TcpClient} with the given id or null if no such player
	 *         exists
	 */
	public TcpClient getClientById(final int _id) {
		final Iterator<TcpClient> it = _clients.iterator();
		TcpClient next, result = null;
		while (it.hasNext() && result == null) {
			next = it.next();
			if (next.getPlayer().getWrappedObject().getId() == _id) {
				result = next;
			}
		}
		return result;
	}

	/**
	 * @return {@link ServerCommand}s that are currently bound
	 */
	public TopLevelCommand<Server> getServerCommands() {
		return _serverCommands;
	}

	/**
	 * @return {@link ClientCommand}s that are currently bound
	 */
	public TopLevelCommand<TcpClient> getClientCommands() {
		return _clientCommands;
	}

	/**
	 * Constructor
	 *
	 * @param _port
	 *            port on which the server should run on
	 * @throws IOException
	 *             if opening the serversocket goes wrong
	 */
	public Server(final String port, final File _mapFile) throws IOException {
		try {
			_port = Integer.parseInt(port);
			_socket = new ServerSocket(_port);
			_map = new ServerMap(_mapFile, this);
			_logger.accept(MessageType.GENERIC, MessageType.INFO, MessageType.ERROR, MessageType.NOTIFICATION,
					MessageType.OUTPUT, MessageType.DEBUG);
			_objectMapper = new ObjectMapper();
			_map.getListeners().add(this);
			getListeners().add(_map);
			_serverCommands.addSubcommand(new HelpCommand());
			_serverCommands.addSubcommand(new KickCommand());
			_serverCommands.addSubcommand(new ListCommand());
			_serverCommands.addSubcommand(new ShutdownCommand());
			_serverCommands.addSubcommand(new InfoCommand());
			_serverCommands.addSubcommand(new SpawnDragonCommand());
			_serverCommands.addSubcommand(new LoggerSensitivityCommand());
			_serverCommands.addSubcommand(new ReloadCommand());
			final ClientCommand ask = new AskCommand(this);
			ask.addSubcommand(new AskDisconnectCommand(this));
			ask.addSubcommand(new AskMoveCommand(this));
			ask.addSubcommand(new AskRenameCommand(this));
			ask.addSubcommand(new AskSayCommand(this));
			ask.addSubcommand(new AskSetCommand(this));
			ask.addSubcommand(new AskChallengeCommand(this));
			ask.addSubcommand(new AskAuthCommand(this));
			final ClientCommand get = new GetCommand(this);
			get.addSubcommand(new GetAllEntitesCommand(this));
			get.addSubcommand(new GetEntityCommand(this));
			get.addSubcommand(new GetMapCommand(this));
			get.addSubcommand(new GetOnlineCountCommand(this));
			get.addSubcommand(new GetOwnIDCommand(this));
			get.addSubcommand(new GetSelfCommand(this));
			get.addSubcommand(new GetServertimeCommand(this));
			get.addSubcommand(new GetUsersCommand(this));
			get.addSubcommand(new GetRankingCommand(this));
			_clientCommands.addSubcommand(ask);
			_clientCommands.addSubcommand(get);
		} catch (final NumberFormatException nfe) {
			_logger.println("Invalid port: " + _port, MessageType.ERROR);
			System.exit(1);
		} catch (final IOException ioe) {
			_logger.println("Cannot use port " + _port + ". Is it already in use?", MessageType.ERROR);
			System.exit(1);
		} catch (final MapException me) {
			_logger.println("Map initialisation error: " + me.getMessage(), MessageType.ERROR);
			System.exit(1);
		}

	}

	public Optional<String> json(Object o) {
		try {
			return Optional.of(getObjectMapper().writeValueAsString(o));
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public Optional<String> json(String label, Object o) {
		Map<String, Object> map = new HashMap<>();
		map.put(label, o);
		return json(map);
	}

	/**
	 * Starts the server and waits for incoming clients
	 */
	public void run() {
		final String colLog = Logger.ANSI_GREEN, colRes = Logger.ANSI_RESET, colInfo = Logger.ANSI_PURPLE;
		final Logger header = new Logger();
		header.accept(MessageType.GENERIC);
		header.println("|----------------------------|");
		header.println("|" + colLog + ".___        _____________   " + colRes + "|");
		header.println("|" + colLog + "|   | _____/ ____\\_____  \\  " + colRes + "|");
		header.println("|" + colLog + "|   |/    \\   __\\  _(__  <  " + colRes + "|");
		header.println("|" + colLog + "|   |   |  |  |   /       \\ " + colRes + "|");
		header.println("|" + colLog + "|___|___|  |__|  /______  / " + colRes + "|");
		header.println("|" + colLog + "         \\/             \\/  " + colRes + "|");
		header.println("|----------------------------|");
		header.println("| " + colInfo + "Serverversion: " + colRes + getVersion());
		header.println("| " + colInfo + "State: " + colRes + serverState);
		header.println("| " + colInfo + "Address: " + colRes + _socket.getInetAddress());
		header.println("| " + colInfo + "Port: " + colRes + _socket.getLocalPort());
		header.println("| " + colInfo + "Map: " + colRes + _map.getMapFilePath());
		header.println("| " + colInfo + "DB: " + colRes + Const.DB_NAME);
		header.println("|----------------------------|");
		header.println("Listening for clients...");
		new Thread(_clock, "ticker").start();
		new Thread(_consoleInput, "console input").start();
		_running = true;
		Socket sock;
		while (_running) {
			try {
				sock = _socket.accept();
				final TcpClient cl = new TcpClient(this, sock);
				new Thread(cl, "client " + cl.getPlayer().getWrappedObject().getId()).start();
			} catch (final IOException ioe) {
				_logger.println("Couldn't open streams. Disconnecting client.", MessageType.ERROR);
			} catch (final Exception e) {
				_logger.println("Unexpected exception:", MessageType.ERROR);
				_logger.printException(e);
			}
		}
	}

	/**
	 * Stops the server gracefully
	 */
	public synchronized void stop() {
		if (_running) {
			_running = false;
			_clock._running = false;
			_consoleInput._running = false;
			broadcast(new ServerMessage("shutting down server"));
			for (final TcpClient cl : _clients) {
				cl.close();
			}
			for (final IServerListener sl : _listeners) {
				sl.onShutdown(this);
			}
			_listeners.unregisterAll();
			System.exit(0);
		}
	}

	/**
	 * Called by a {@link TcpClient} when he is ready and done setting itself up.
	 * The new presence of the {@link TcpClient}s {@link Player} will then be
	 * broadcasted to all other {@link Player}s He will then start to receive ticks
	 * from the server too.
	 *
	 * @param _client
	 *            ready {@link TcpClient}
	 */
	public void ready(final TcpClient _client) {
		broadcast(_client.getPlayer().getWrappedObject(), ServerConst.UPD);
		_listeners.add(_client.getPlayer());
	}

	/**
	 * Called by a {@link TcpClient} when he wishes to be terminated. He will the
	 * proceed to shut himself down but gives the server the opportunity to
	 * broadcast this event. Will also remove the player from the list of listeners
	 *
	 * @param _client
	 *            terminating {@link TcpClient}
	 */
	synchronized public void terminate(final TcpClient _client) {
		_listeners.remove(_client.getPlayer());
		broadcast(_client.getPlayer().getWrappedObject(), ServerConst.DEL);
	}

	/**
	 * Adds a new client thread to the list of clients
	 *
	 * @param _client
	 *            new client
	 */
	public void addClient(final TcpClient _client) {
		_clients.add(_client);
	}

	/**
	 * Removes a client from the server
	 *
	 * @param _client
	 *            client to remove
	 */
	public void removeClient(final TcpClient _client) {
		this._clients.remove(_client);
	}

	/**
	 * Processes a request from the console.
	 *
	 * @param _cmd
	 *            request string from the console
	 */
	public void processServerCommand(final String _cmd) {
		final StringBuilder mes = new StringBuilder();
		_serverCommands.execute(this, _cmd, mes);
		_logger.println(mes.toString(), MessageType.OUTPUT);
	}

	/**
	 * Processes a request sent by a client.
	 *
	 * @param _src
	 *            the source {@link TcpClient} from which the request came
	 * @param _req
	 *            the request string itself
	 */
	public void processClientCommand(final TcpClient _src, final String _req) {
		final StringBuilder mes = new StringBuilder();
		final int result = _clientCommands.execute(_src, _req, mes);
		if (0 == result) {
			_src.sendUnknown(_req);
		} else if (-1 == result) {
			_src.sendInvalid();
		}
		if (_src.pendingMessage()) {
			_src.endMessage();
		}
		final String messtr = mes.toString().trim();
		if (!messtr.equals("")) {
			_logger.println(mes.toString(), MessageType.OUTPUT);
		}
	}

	/**
	 * Broadcasts a {@link ITokenizable} object to all players Broadcasting is
	 * synchronized as many {@link TcpClient}s (which are one thread each) could
	 * issue a broadcast which would lead to mingled messages
	 *
	 * @param _tok
	 *            {@link ITokenizable} object
	 */
	public synchronized void broadcast(final ITokenizable _tok) {
		TcpClient client;
		final Iterator<TcpClient> it = this._clients.iterator();
		String json;
		try {
			json = getObjectMapper().writeValueAsString(_tok);
			while (it.hasNext()) {
				client = it.next();
				if (!client.isClosed()) {
					// client.flushTokenizable(_tok);
					client.send(json);
				}
			}
		} catch (final JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Broadcasts a {@link ITokenizable} object to all players with a surrounding
	 * tag
	 *
	 * @param _tok
	 *            {@link ITokenizable} object
	 * @param type
	 *            surrounding tag if any. This allows us to send typed messages,
	 *            such as updates
	 * @throws Exception
	 */

	/*
	 * @Deprecated public synchronized void broadcast(final ITokenizable _tok, final
	 * String _type) throws Exception { if (true) { throw new
	 * Exception("Don't use this method"); } /* TcpClient client; final
	 * Iterator<TcpClient> it = clients.iterator(); while (it.hasNext()) { client =
	 * it.next(); if (!client.isClosed()) { client.beginMessage();
	 * client.send(ServerConst.BEGIN + _type); client.sendTokenizable(_tok);
	 * client.send(ServerConst.END + _type); client.endMessage();
	 * 
	 * }
	 * 
	 * }
	 */

	// FIXME: replace above version with this one
	public synchronized void broadcast(Object o, final String type) {
		TcpClient client;
		final Iterator<TcpClient> it = _clients.iterator();
		while (it.hasNext()) {
			client = it.next();
			if (!client.isClosed()) {
				final Map<String, Object> mes = new HashMap<>();
				mes.put(type, o);
				client.send(json(mes).get());
			}
		}
	}

	@Override
	public ListenerSet<IServerListener> getListeners() {
		return _listeners;
	}

	@Override
	public void onToggleHuntable(final MapCell cell, final boolean huntable) {
		broadcast(cell, ServerConst.UPD);
	}

	@Override
	public void onSpawnDragon(final MapCell cell, final Dragon dragon) {
		// to preserve storage to DB should it get implemented one day...
		new ServerDragon(dragon, this, true);
		broadcast(dragon, ServerConst.UPD);
	}

	@Override
	public void onMove(final ServerDragon dragon, final Vector2D oldpos, final Vector2D newpos) {
		broadcast(dragon.getWrappedObject(), ServerConst.UPD);
	}

	public static void main(final String[] args) throws IOException {
		String port, map;
		if (args.length < 2) {
			port = Configuration.getInstance().getProperty(Configuration.DEFAULT_SERVER_PORT);
			map = Configuration.getInstance().getProperty(Configuration.DEFAULT_MAP_PATH);
			System.out.println(String.format(
					"Application was not called with 2 arguments (1: port to run on, 2: path to map) Defaulting from config file (%s)...",
					Const.PATH_CONF));
		} else {
			port = args[0];
			map = args[1];
		}
		try {
			System.out.println(String.format("Attempting to run server on port %s...", port));
			new Server(port, new File(map)).run();
		} catch (final Exception e) {
			System.err.println("Failed to start server. Aborting.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String getVersion() {
		return String.format("%d.%d", serverVersionMajor, serverVersionMinor);
	}
}
