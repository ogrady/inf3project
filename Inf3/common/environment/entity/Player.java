package environment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import util.Configuration;
import util.SyncedMap;

/**
 * Players which hold the information about connected users, such as their current decisions for minigames and their total points
 * @author Daniel
 *
 */
final public class Player extends Entity {
	// db here
	public static SyncedMap<Player> instances = new SyncedMap<Player>();
	@JsonIgnore
	protected StaghuntDecision staghuntDecision;
	@JsonIgnore
	protected DragonDecision dragonDecision;
	@JsonIgnore
	protected SkirmishDecision skirmishDecision;	
	protected int points;
	
	/**
	 * @return current decision for the staghunt-game
	 */
	public StaghuntDecision getStaghuntDecision() {
		return staghuntDecision;
	}

	/**
	 * @param _staghuntDecision the decision for the staghunt-game that should be chosen for the next round
	 */
	public void setStaghuntDecision(StaghuntDecision _staghuntDecision) {
		staghuntDecision = _staghuntDecision;
	}

	/**
	 * @return current decision for the dragonfight-game
	 */
	public DragonDecision getDragonDecision() {
		return dragonDecision;
	}

	/**
	 * @param _dragonDecision the decision for the dragonfight-game that should be chosen for the next round
	 */
	public void setDragonDecision(DragonDecision _dragonDecision) {
		dragonDecision = _dragonDecision;
	}

	/**
	 * @return current decision for the player-vs-player-game
	 */
	public SkirmishDecision getSkirmishDecision() {
		return skirmishDecision;
	}

	/**
	 * @param _battleDecision the decision for the skirmish-game that should be chosen for the next round
	 */
	public void setSkirmishDecision(SkirmishDecision _battleDecision) {
		skirmishDecision = _battleDecision;
	}

	/**
	 * @return current amount of points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * @param _points new total points
	 */
	public void setPoints(int _points) {
		points = _points;
	}
	
	/**
	 * Convenience method to add / remove a certain amount of points
	 * @param _amount delta of points (negative to substract)
	 */
	public void addPoints(int _amount) {
		points += _amount;
	}

	/**
	 * Constructor
	 * for new players where the id is yet unknown
	 * @param _x x-position
	 * @param _y y-position
	 * @param _name name
	 */
	public Player(int _x, int _y, String _name) {
		super(_x, _y, _name);
		init();
	}
	
	/**
	 * Constructor 
	 * for existing players where the id is already known
	 * @param _id id
	 * @param _x x-position
	 * @param _y y-position
	 * @param _name name
	 * @param _busy whether or not the player is busy
	 * @param _points current points
	 */
	public Player(int _id, int _x, int _y, String _name, boolean _busy, int _points) {
		super(_id, _x, _y, _name, _busy);
		points = _points;
		init();
	}
	
	/**
	 * Initialize the player by putting default decisions for the minigames and putting the
	 * player itself into the instance-map
	 */
	private void init() {
		Configuration conf = Configuration.getInstance();
		staghuntDecision = StaghuntDecision.valueOf(conf.getProperty(Configuration.DEFAULT_STAGHUNT));
		dragonDecision = DragonDecision.valueOf(conf.getProperty(Configuration.DEFAULT_DRAGON));
		skirmishDecision = SkirmishDecision.valueOf(conf.getProperty(Configuration.DEFAULT_SKIRMISH));
		Player.instances.put(this.id, this);
	}
	
	@Override
	public void destruct() {
		super.destruct();
		Player.instances.remove(this.id);
	}
	
	public String toVerboseString() {
		return String.format("NAME: %s \r\nID: %d \r\nPOSITION: (%d|%d) \r\nSTAGHUNT: %s \r\nSKIRMISH: %s \r\nDRAGON: %s \r\nPOINTS: %d",
				getDescription(), id, position.x, position.y, staghuntDecision, skirmishDecision, dragonDecision, points 
				);
	}
}
