package tokenizer;

import java.util.List;

/**
 * Interface for objects that can be sent over the network as plaintext.
 * 
 * @author Daniel
 */
@Deprecated
public interface ITokenizable {
	/**
	 * Breaks the object itself down into an array of lines with xml-structure.<br>
	 * Said array has the form:
	 * <p>
	 * 
	 * <pre>
	 * A {@link ITokenizable} should also keep XML structure which means:
	 * begin:[object]
	 *   {[attribute]:[value]}
	 * end:[object]
	 * </pre>
	 * 
	 * where attributes can be structured objects themselves.
	 * </p>
	 * 
	 * @return
	 */
	public List<String> tokenize();
}
