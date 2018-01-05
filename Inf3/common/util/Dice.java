package util;

import java.util.Random;

/**
 * Dice to roll for random values. They don't just generate random numbers but
 * rather check whether you are lucky or not. Every dice stores a maximum value.
 * For each roll you give in a certain chance. The smaller the difference
 * between maximum and chance, the bigger your chance to roll succesfully. This
 * is used to spawn dragons and make cells huntable.
 * 
 * @author Daniel
 */
public class Dice {
	private final Random _rand;
	private final int _max;

	/**
	 * Roll the dice
	 * 
	 * @param chance
	 *            the higher this value, the better your chance to roll sucessfully
	 * @return whether you were lucky or not
	 */
	public boolean roll(int chance) {
		// +1 to avoid having nextInt(0) when a config yields 100% which causes an error
		return _rand.nextInt(Math.abs(_max - chance) + 1) == 0;
	}

	/**
	 * Constructor takes config value for max
	 */
	public Dice() {
		this(Configuration.getInstance().getInteger(Configuration.MAX_PERCENT));
	}

	/**
	 * Constructor
	 * 
	 * @param _max
	 *            the higher this value the, the harder it is to roll succesfully.
	 *            Or in other words: this value represents 100%. The higher this
	 *            value the finer the granularity of the dice.
	 */
	public Dice(int max) {
		_max = max;
		_rand = new Random();
	}
}
