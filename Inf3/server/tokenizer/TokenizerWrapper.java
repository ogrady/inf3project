package tokenizer;

import java.util.List;

/**
 * Wraps an object with a tokenizer using the adapter-pattern
 * @author Daniel
 *
 * @param <T> type of object that should be tokenized by the wrapped tokenizer
 */
public class TokenizerWrapper<T> implements ITokenizable {
	private Tokenizer<T> tokenizer;
	private T tokenizable;
	
	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(tokenizable);
	}
	
	/**
	 * Constructor
	 * @param _tokenizer the tokenizer responsible for tokenizing the tokenizable object
	 * @param _tokenizable the tokenizable object
	 */
	public TokenizerWrapper(Tokenizer<T> _tokenizer, T _tokenizable) {
		this.tokenizer = _tokenizer;
		this.tokenizable = _tokenizable;
	}
}
