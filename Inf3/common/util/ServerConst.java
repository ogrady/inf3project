package util;

public class ServerConst {
	private ServerConst() {
	}

	public static final String SERVER = "server", VERSION = "ver:", CMD_SEPARATOR = ":",
			// = server commands start here
			SC_HELP = "help", SC_KICK = "kick:", SC_KICK_NAME = "name:", SC_KICK_ID = "id:", SC_LIST = "list:",
			SC_LIST_PLAYER = "player", SC_LIST_CLIENT = "client", SC_LIST_ENTITIES = "entities", SC_SHUTDOWN = "stop",
			SC_INFO = "info:", SC_SPAWN_DRAGON = "spawn", SC_LOGGER = "logger:", SC_LOGGER_ADD = "add:",
			SC_LOGGER_REMOVE = "rmv:", SC_RELOAD = "reload",
			// = client commands start here
			// prefix for a request to request information from the server
			CC_GET = "get:",
			// request to send the mapdata
			GET_MAP = "map",
			// online users count
			GET_COUNT = "online", GET_USERS = "players", GET_ME = "me", GET_MY_ID = "myid", GET_ENTITY = "ent:",
			GET_ENTITIES = "ents", GET_TIME = "time", GET_RANKING = "ranking",
			// prefix to ask the server to do something, like moving the own actor
			CC_ASK = "ask:", ASK_MOVE = "mv:", MOVE_UP = "up", MOVE_DOWN = "dwn", MOVE_LEFT = "lft", MOVE_RIGHT = "rgt",
			ASK_RENAME = "rn:", ASK_BYE = "bye",
			// prefix to send a message to all online players
			ASK_SAY = "say:", ASK_SET = "set:", SET_DRAGON = "dragon:", SET_SKIRMISH = "skirm:",
			SET_STAGHUNT = "shunt:", ASK_CHAL = "chal:", CHAL_DRAGON = "dragon:", CHAL_STAGHUNT = "shunt:",
			CHAL_SKIRMISH = "skirm:", ASK_AUTH = "auth:",
			// = server prefixes start here
			// prefixes to mark the begin/end of multiline answers
			BEGIN = "begin:", END = "end:",
			// answer to a former ASK request
			ANS = "ans", ANS_MES = "mes", ANS_CODE = "code", UPD = "upd", DEL = "del", ANS_YES = "ok", ANS_NO = "no", ANS_INVALID = "invalid",
			ANS_COUNT = GET_COUNT, ANS_TIME = GET_TIME, ANS_MAP = GET_MAP, ANS_USERS = GET_USERS, ANS_ENTITIES = "ents",
			ANS_UNKNOWN = "unknown:", ANS_MY_ID = "yourid", ANS_ME = "you";
	
	public static final Integer
		CODE_ANS_YES = 0,
		CODE_ANS_NO = 1,
		CODE_ANS_INVALID = 2,
		CODE_ANS_UNKNOWN = 3;
}
