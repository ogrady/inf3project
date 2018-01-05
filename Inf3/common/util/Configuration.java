package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import environment.entity.DragonDecision;
import environment.entity.SkirmishDecision;
import environment.entity.StaghuntDecision;

public class Configuration extends Properties {
	private static final long serialVersionUID = 1L;
	// keys
	public static final String UPDATE_INTERVAL = "mapcell_update_interval", TICK_DELAY = "tick_delay",
			MAX_DRAGON = "max_dragon", MAX_HUNTABLE = "max_huntable", MINIGAME_ROUND_DELAY = "minigame_round_delay",
			MAX_PERCENT = "max_percent", HUNTABLE_CHANCE = "huntable_chance", DRAGON_CHANCE = "dragon_chance",
			DEFAULT_SKIRMISH = "default_skirmish", DEFAULT_DRAGON = "default_dragon",
			DEFAULT_STAGHUNT = "default_staghunt", DRAGON_MOVE_INTERVAL = "dragon_move_interval",
			DRAGON_MOVE_CHANCE = "dragon_move_chance", DB_FOLDER = "db_file_path",
			DEFAULT_SERVER_PORT = "default_server_port", DEFAULT_MAP_PATH = "default_map_path",
			SKIRMISH_ROUNDS = "skirmish_rounds", DRAGON_ROUNDS = "dragon_rounds", STAGHUNT_ROUNDS = "staghunt_rounds",
			ADMIN_PASSWORD = "admin_password";
	private static final Properties defaultConfig = new Properties();
	static {
		defaultConfig.setProperty(UPDATE_INTERVAL, "100000");
		defaultConfig.setProperty(TICK_DELAY, "25");
		defaultConfig.setProperty(MAX_DRAGON, "3");
		defaultConfig.setProperty(MAX_HUNTABLE, "20");
		defaultConfig.setProperty(MINIGAME_ROUND_DELAY, "10000");
		defaultConfig.setProperty(MAX_PERCENT, "1000000");
		defaultConfig.setProperty(HUNTABLE_CHANCE, "100");
		defaultConfig.setProperty(DRAGON_CHANCE, "1");
		defaultConfig.setProperty(DEFAULT_SKIRMISH, SkirmishDecision.SWORD.toString());
		defaultConfig.setProperty(DEFAULT_DRAGON, DragonDecision.FIGHT.toString());
		defaultConfig.setProperty(DEFAULT_STAGHUNT, StaghuntDecision.STAG.toString());
		defaultConfig.setProperty(DRAGON_MOVE_INTERVAL, "100000");
		defaultConfig.setProperty(DRAGON_MOVE_CHANCE, "100");
		defaultConfig.setProperty(DB_FOLDER, "db/");
		defaultConfig.setProperty(DEFAULT_SERVER_PORT, "1337");
		defaultConfig.setProperty(DEFAULT_MAP_PATH, "defmap.bmp");
		defaultConfig.setProperty(SKIRMISH_ROUNDS, "5");
		defaultConfig.setProperty(DRAGON_ROUNDS, "5");
		defaultConfig.setProperty(STAGHUNT_ROUNDS, "5");
		defaultConfig.setProperty(ADMIN_PASSWORD, "admin123");
	}
	private static final Configuration instance = new Configuration();

	public static Configuration getInstance() {
		return instance;
	}

	private Configuration() {
		super(defaultConfig);
		load();
	}

	/**
	 * Tries to parse the given property to int
	 * 
	 * @param _key
	 *            key of the property
	 * @return int value of desired property if possible
	 */
	public int getInteger(String _key) {
		return Integer.parseInt(this.getProperty(_key));
	}

	/**
	 * Tries to parse the given property to long
	 * 
	 * @param _key
	 *            key of the property
	 * @return long value of desired property if possible
	 */
	public long getLong(String _key) {
		return Long.parseLong(this.getProperty(_key));
	}

	/**
	 * (Re-)loads the config file
	 * 
	 * @return true, if the config was successfully loaded. False implies that the
	 *         default config was loaded
	 */
	public boolean load() {
		boolean success = false;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(Const.PATH_CONF));
			load(in);
			in.close();
			success = true;
		} catch (FileNotFoundException e) {
			System.err.println(String.format("Could not find config file at '%s'. Defaulting.", Const.PATH_CONF));
		} catch (IOException e) {
			System.err.println(String.format("Could not load config from '%s'. Defaulting.", Const.PATH_CONF));
		}
		if (!success) {
			BufferedOutputStream out;
			try {
				out = new BufferedOutputStream(new FileOutputStream(Const.PATH_CONF));
				store(out, "Configuration file for the INF3-Server");
			} catch (FileNotFoundException e) {
				System.err.println(String.format(
						"Failed to store default config in directory '%s'. Please specify a file instead.",
						Const.PATH_CONF));
			} catch (IOException e) {
				System.err.println(String.format("Failed to store default config at '%s'", Const.PATH_CONF));
			}
		}
		return success;
	}
}
