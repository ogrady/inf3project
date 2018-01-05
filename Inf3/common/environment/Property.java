package environment;

import java.util.ArrayList;

import tokenizer.ITokenizable;

/**
 * {@link Property} of a {@link MapCell}, such as whether it is walkable or not
 * 
 * @author Daniel
 */
public enum Property implements ITokenizable {
	WALKABLE, WALL, FOREST, WATER, HUNTABLE;

	public static final String PROPERTY = "prop";

	@Override
	public ArrayList<String> tokenize() {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(this.name());
		return tokens;
	}

	public int getValue() {
		return (int) Math.pow(2, this.ordinal());
	}
}
