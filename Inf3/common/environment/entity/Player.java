package environment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import util.Configuration;
import util.SyncedMap;

/**
 * Players which hold the information about connected users, such as their
 * current decisions for minigames and their total points
 * 
 * @author Daniel
 *
 */
final public class Player extends Entity {
	// db here
	public static SyncedMap<Player> instances = new SyncedMap<>();
	@JsonIgnore
	protected StaghuntDecision _staghuntDecision;
	@JsonIgnore
	protected DragonDecision _dragonDecision;
	@JsonIgnore
	protected SkirmishDecision _skirmishDecision;
	protected int _points;

	/**
	 * @return current decision for the staghunt-game
	 */
	public StaghuntDecision getStaghuntDecision() {
		return _staghuntDecision;
	}

	/**
	 * @param _staghuntDecision
	 *            the decision for the staghunt-game that should be chosen for the
	 *            next round
	 */
	public void setStaghuntDecision(StaghuntDecision staghuntDecision) {
		_staghuntDecision = staghuntDecision;
	}

	/**
	 * @return current decision for the dragonfight-game
	 */
	public DragonDecision getDragonDecision() {
		return _dragonDecision;
	}

	/**
	 * @param _dragonDecision
	 *            the decision for the dragonfight-game that should be chosen for
	 *            the next round
	 */
	public void setDragonDecision(DragonDecision dragonDecision) {
		_dragonDecision = dragonDecision;
	}

	/**
	 * @return current decision for the player-vs-player-game
	 */
	public SkirmishDecision getSkirmishDecision() {
		return _skirmishDecision;
	}

	/**
	 * @param _battleDecision
	 *            the decision for the skirmish-game that should be chosen for the
	 *            next round
	 */
	public void setSkirmishDecision(SkirmishDecision battleDecision) {
		_skirmishDecision = battleDecision;
	}

	/**
	 * @return current amount of points
	 */
	public int getPoints() {
		return _points;
	}

	/**
	 * @param _points
	 *            new total points
	 */
	public void setPoints(int points) {
		_points = points;
	}

	/**
	 * Convenience method to add / remove a certain amount of points
	 * 
	 * @param amount
	 *            delta of points (negative to substract)
	 */
	public void addPoints(int amount) {
		_points += amount;
	}

	/**
	 * Constructor for new players where the id is yet unknown
	 * 
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 * @param name
	 *            name
	 */
	public Player(int x, int y, String name) {
		super(x, y, name);
		init();
	}

	/**
	 * Constructor for existing players where the id is already known
	 * 
	 * @param id
	 *            id
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 * @param name
	 *            name
	 * @param busy
	 *            whether or not the player is busy
	 * @param points
	 *            current points
	 */
	public Player(int id, int x, int y, String name, boolean busy, int points) {
		super(id, x, y, name, busy);
		_points = points;
		init();
	}

	/**
	 * Initialize the player by putting default decisions for the minigames and
	 * putting the player itself into the instance-map
	 */
	private void init() {
		final Configuration conf = Configuration.getInstance();
		_staghuntDecision = StaghuntDecision.valueOf(conf.getProperty(Configuration.DEFAULT_STAGHUNT));
		_dragonDecision = DragonDecision.valueOf(conf.getProperty(Configuration.DEFAULT_DRAGON));
		_skirmishDecision = SkirmishDecision.valueOf(conf.getProperty(Configuration.DEFAULT_SKIRMISH));
		Player.instances.put(this._id, this);
	}

	@Override
	public void destruct() {
		super.destruct();
		Player.instances.remove(this._id);
	}

	public String toVerboseString() {
		return String.format(
				"NAME: %s \r\nID: %d \r\nPOSITION: (%d|%d) \r\nSTAGHUNT: %s \r\nSKIRMISH: %s \r\nDRAGON: %s \r\nPOINTS: %d",
				getDescription(), _id, _position.x, _position.y, _staghuntDecision, _skirmishDecision, _dragonDecision,
				_points);
	}
}
