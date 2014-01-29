package util;

public class Const {
	// PARSER VALUES
	public static final String
	// entity
	PAR_BEGIN = "begin:",
			PAR_END = "end:",
			PAR_ID = "id:",
			PAR_BUSY = "busy:",
			PAR_TYPE = "type:",
			PAR_DESCRIPTION = "desc:",
			PAR_XPOS = "x:",
			PAR_YPOS = "y:",
			PAR_ENTITY = "entity",
			// player
			PAR_PLAYER = "player",
			PAR_POINTS = "points:",
			// dragon
			PAR_DRAGON = "dragon",
			// map
			PAR_MAP = "map",
			PAR_WIDTH = "width:",
			PAR_HEIGHT = "height:",
			PAR_CELLS = "cells",
			// map cell
			PAR_CELL = "cell",
			PAR_ROW = "row:",
			PAR_COLUMN = "col:",
			PAR_PROPS = "props",
			// message
			PAR_MESSAGE = "mes",
			PAR_MES_ID = "srcid:",
			PAR_SENDER = "src:",
			PAR_TEXT = "txt:",
			// arena
			PAR_OPPONENT = "opponent",
			PAR_RESULT = "result",
			PAR_ROUND = "round:",
			PAR_OPPONENTS = "opponents",
			PAR_RUNNING = "running:",
			PAR_DELAY = "delay:",
			PAR_TOTAL = "total:",
			PAR_DECISION = "decision:",
			// challenge
			PAR_CHALLENGE = "challenge",
			PAR_TYPE_DRAGON = "DRAGON",
			PAR_TYPE_STAGHUNT = "STAGHUNT",
			PAR_TYPE_SKIRMISH = "SKIRMISH",
			PAR_ACCEPTED = "accepted:",
			// command line options
			OPT_MAP = "map",
			OPT_PORT = "port",
			OPT_DESC_MAP = "path for the map bmp",
			OPT_DESC_PORT = "port on which the server should run on",
			// path
			PATH_RSC = "gui/rsc/", PATH_CONF = "conf.properties";
	public static final String DB_NAME = "UNUSED";

	/*
	public static final ExtObjectContainer db;
	static {
		DB_NAME = Configuration.getInstance().getProperty(Configuration.DB_FOLDER)+System.currentTimeMillis()+".db";
		EmbeddedConfiguration dbconf = Db4oEmbedded.newConfiguration();
		//dbconf.common().updateDepth(5);
		dbconf.common().add(new TransparentPersistenceSupport(new DeactivatingRollbackStrategy()));
		dbconf.common().exceptionsOnNotStorable(false);
		new File(Configuration.getInstance().getProperty(Configuration.DB_FOLDER)).mkdirs();
		db = Db4oEmbedded.openFile(dbconf, DB_NAME).ext();
		//Runtime.getRuntime().addShutdownHook(new DeleteDBOnShutdown(DB_NAME));
	}*/

	private Const() {
	}
}
