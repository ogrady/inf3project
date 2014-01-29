package tokenizer;

import java.util.List;

public abstract class Tokenizer<T> {
	public abstract List<String> tokenize(T obj);
}
