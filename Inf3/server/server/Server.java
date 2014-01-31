package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import environment.ServerMapCell;
import environment.entity.Dragon;
import environment.entity.Player;
import environment.wrapper.ServerDragon;
import environment.wrapper.ServerMap;
import exception.MapException;

/**
 * Server for the game
 * 
 * @author Daniel
 */
public class Server implements ITokenizable, IListenable<IServerListener>,
		IMapListener, IDragonListener {
	private static final ExecutorService threadPool = Executors
			.newCachedThreadPool();
	public static final int serverVersionMajor = 1;
	public static final int serverVersionMinor = 9;
	public static final ServerState serverState = ServerState.EXPERIMENTAL;

	private boolean running = false;
	private int port;
	private ServerMap map;
	private ServerSocket socket;
	private final ServerClock ticker = new ServerClock(this);
	private final Logger logger = new TimeLogger();
	private final ListenerSet<IServerListener> listeners = new ListenerSet<IServerListener>();;
	private final ConsoleInput consoleInput = new ConsoleInput(this);
	private final CopyOnWriteArrayList<TcpClient> clients = new CopyOnWriteArrayList<TcpClient>();
	private final TopLevelCommand<Server> serverCommands = new TopLevelCommand<Server>();
	private final TopLevelCommand<TcpClient> clientCommands = new TopLevelCommand<TcpClient>();

	/**
	 * @return the threadpool of the server for reusable tasks
	 */
	public ExecutorService getThreadpool() {
		return threadPool;
	}

	/**
	 * @return {@link Logger} that is currently in use
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return the {@link ServerMap} the server is using
	 */
	public ServerMap getMap() {
		return map;
	}

	/**
	 * @return a {@link CopyOnWriteArrayList} with {@link TcpClient}s that are
	 *         currently running
	 */
	public CopyOnWriteArrayList<TcpClient> getClients() {
		return clients;
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
		final Iterator<TcpClient> it = clients.iterator();
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
		return serverCommands;
	}

	/**
	 * @return {@link ClientCommand}s that are currently bound
	 */
	public TopLevelCommand<TcpClient> getClientCommands() {
		return clientCommands;
	}

	/**
	 * Constructor
	 * 
	 * @param _port
	 *            port on which the server should run on
	 * @throws IOException
	 *             if opening the serversocket goes wrong
	 */
	public Server(final String _port, final File _mapFile) throws IOException {
		try {
			port = Integer.parseInt(_port);
			socket = new ServerSocket(port);
			map = new ServerMap(_mapFile, this);
			logger.accept(MessageType.GENERIC, MessageType.INFO,
					MessageType.ERROR, MessageType.NOTIFICATION,
					MessageType.OUTPUT, MessageType.DEBUG);
			map.getListeners().add(this);
			getListeners().add(map);
			serverCommands.addSubcommand(new HelpCommand());
			serverCommands.addSubcommand(new KickCommand());
			serverCommands.addSubcommand(new ListCommand());
			serverCommands.addSubcommand(new ShutdownCommand());
			serverCommands.addSubcommand(new InfoCommand());
			serverCommands.addSubcommand(new SpawnDragonCommand());
			serverCommands.addSubcommand(new LoggerSensitivityCommand());
			serverCommands.addSubcommand(new ReloadCommand());
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
			clientCommands.addSubcommand(ask);
			clientCommands.addSubcommand(get);
		} catch (final NumberFormatException nfe) {
			logger.println("Invalid port: " + _port, MessageType.ERROR);
			System.exit(1);
		} catch (final IOException ioe) {
			logger.println("Cannot use port " + _port
					+ ". Is it already in use?", MessageType.ERROR);
			System.exit(1);
		} catch (final MapException me) {
			logger.println("Map initialisation error: " + me.getMessage(),
					MessageType.ERROR);
			// me.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Starts the server and waits for incoming clients
	 */
	public void run() {
		final String colLog = Logger.ANSI_GREEN, colRes = Logger.ANSI_RESET, colInfo = Logger.ANSI_PURPLE;
		final Logger header = new Logger();
		header.accept(MessageType.GENERIC);
		header.println("|----------------------------|");
		header.println("|" + colLog + ".___        _____________   " + colRes
				+ "|");
		header.println("|" + colLog + "|   | _____/ ____\\_____  \\  " + colRes
				+ "|");
		header.println("|" + colLog + "|   |/    \\   __\\  _(__  <  " + colRes
				+ "|");
		header.println("|" + colLog + "|   |   |  |  |   /       \\ " + colRes
				+ "|");
		header.println("|" + colLog + "|___|___|  |__|  /______  / " + colRes
				+ "|");
		header.println("|" + colLog + "         \\/             \\/  " + colRes
				+ "|");
		header.println("|----------------------------|");
		header.println("| " + colInfo + "Serverversion: " + colRes
				+ getVersion());
		header.println("| " + colInfo + "State: " + colRes + serverState);
		header.println("| " + colInfo + "Address: " + colRes
				+ socket.getInetAddress());
		header.println("| " + colInfo + "Port: " + colRes
				+ socket.getLocalPort());
		header.println("| " + colInfo + "Map: " + colRes + map.getMapFilePath());
		header.println("| " + colInfo + "DB: " + colRes + Const.DB_NAME);
		header.println("|----------------------------|");
		header.println("Listening for clients...");
		threadPool.execute(ticker);
		threadPool.execute(consoleInput);
		running = true;
		Socket sock;
		while (running) {
			try {
				sock = socket.accept();
				threadPool.execute(new TcpClient(this, sock));
				/*
				client.beginMessage();
				client.sendTokenizable(this);
				client.endMessage();*/
			} catch (final IOException ioe) {
				logger.println("Couldn't open streams. Disconnecting client.",
						MessageType.ERROR);
			} catch (final Exception e) {
				logger.println("Unexpected exception:", MessageType.ERROR);
				logger.printException(e);
			}
		}
	}

	/**
	 * Stops the server gracefully
	 */
	public synchronized void stop() {
		if (running) {
			running = false;
			ticker.running = false;
			consoleInput.running = false;
			broadcast(new ServerMessage("shutting down server"));
			for (final TcpClient cl : clients) {
				cl.close();
			}
			for (final IServerListener sl : listeners) {
				sl.onShutdown(this);
			}
			listeners.unregisterAll();
			System.exit(0);
		}
	}

	/**
	 * Called by a {@link TcpClient} when he is ready and done setting itself
	 * up. The new presence of the {@link TcpClient}s {@link Player} will then
	 * be broadcasted to all other {@link Player}s He will then start to receive
	 * ticks from the server too.
	 * 
	 * @param _client
	 *            ready {@link TcpClient}
	 */
	public void ready(final TcpClient _client) {
		broadcast(_client.getPlayer(), ServerConst.UPD);
		listeners.add(_client.getPlayer());
	}

	/**
	 * Called by a {@link TcpClient} when he wishes to be terminated. He will
	 * the proceed to shut himself down but gives the server the opportunity to
	 * broadcast this event. Will also remove the player from the list of
	 * listeners
	 * 
	 * @param _client
	 *            terminating {@link TcpClient}
	 */
	synchronized public void terminate(final TcpClient _client) {
		listeners.remove(_client.getPlayer());
		broadcast(_client.getPlayer(), ServerConst.DEL);
	}

	/**
	 * Adds a new client thread to the list of clients
	 * 
	 * @param _client
	 *            new client
	 */
	public void addClient(final TcpClient _client) {
		clients.add(_client);
	}

	/**
	 * Removes a client from the server
	 * 
	 * @param _client
	 *            client to remove
	 */
	public void removeClient(final TcpClient _client) {
		this.clients.remove(_client);
	}

	/**
	 * Processes a request from the console.
	 * 
	 * @param _cmd
	 *            request string from the console
	 */
	public void processServerCommand(final String _cmd) {
		final StringBuilder mes = new StringBuilder();
		serverCommands.execute(this, _cmd, mes);
		logger.println(mes.toString(), MessageType.OUTPUT);
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
		final int result = clientCommands.execute(_src, _req, mes);
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
			logger.println(mes.toString(), MessageType.OUTPUT);
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
		final Iterator<TcpClient> it = this.clients.iterator();
		while (it.hasNext()) {
			client = it.next();
			if (!client.isClosed()) {
				client.flushTokenizable(_tok);
			}
		}
	}

	/**
	 * Broadcasts a {@link ITokenizable} object to all players with a
	 * surrounding tag
	 * 
	 * @param _tok
	 *            {@link ITokenizable} object
	 * @param _type
	 *            surrounding tag if any. This allows us to send typed messages,
	 *            such as updates
	 */
	public synchronized void broadcast(final ITokenizable _tok,
			final String _type) {
		TcpClient client;
		final Iterator<TcpClient> it = clients.iterator();
		while (it.hasNext()) {
			client = it.next();
			if (!client.isClosed()) {
				client.beginMessage();
				client.send(ServerConst.BEGIN + _type);
				client.sendTokenizable(_tok);
				client.send(ServerConst.END + _type);
				client.endMessage();
			}
		}
	}

	@Override
	public ArrayList<String> tokenize() {
		final ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN + ServerConst.SERVER);
		tokens.add(ServerConst.VERSION + getVersion());
		tokens.add(ServerConst.END + ServerConst.SERVER);
		return tokens;
	}

	@Override
	public ListenerSet<IServerListener> getListeners() {
		return listeners;
	}

	@Override
	public void onToggleHuntable(final MapCell cell, final boolean huntable) {
		broadcast(new ServerMapCell(cell, this), ServerConst.UPD);
	}

	@Override
	public void onSpawnDragon(final MapCell cell, final Dragon dragon) {
		broadcast(new ServerDragon(dragon, this, true), ServerConst.UPD);
	}

	@Override
	public void onMove(final ServerDragon dragon, final Vector2D oldpos,
			final Vector2D newpos) {
		broadcast(dragon, ServerConst.UPD);
	}

	public static void main(final String[] args) throws IOException {
		String port, map;
		if (args.length < 2) {
			port = Configuration.getInstance().getProperty(
					Configuration.DEFAULT_SERVER_PORT);
			map = Configuration.getInstance().getProperty(
					Configuration.DEFAULT_MAP_PATH);
			System.out
					.println(String
							.format("Application was not called with 2 arguments (1: port to run on, 2: path to map) Defaulting from config file (%s)...",
									Const.PATH_CONF));
		} else {
			port = args[0];
			map = args[1];
		}
		try {
			System.out.println(String.format(
					"Attempting to run server on port %s...", port));
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
