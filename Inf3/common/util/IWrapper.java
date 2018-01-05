package util;

import tokenizer.Tokenizer;

/**
 * Wrappers are good for storing the desired object alongside with additional
 * objects, like {@link Renderer}s or {@link Tokenizer}s. Before, we had the
 * diamond-problem with Entity, ClientEntity, ServerEntity, ClientPlayer etc.
 * where every ClientFoo had a {@link Renderer} and every ServerFoo had a
 * {@link Tokenizer}. We then had the problem that we could not clearly tell
 * which kind of Foo we had and lost the possibility to do polymorphism (we had
 * to do implicit casts). We now just throw the wrapped object which holds the
 * basic information plus the wrapper which stores the additional stuff we need,
 * based on whether it is ClientFoo or ServerFoo.
 * 
 * @author Daniel
 * @param <W>
 */
public interface IWrapper<W> {
	W getWrappedObject();

	void setWrappedObject(W _wrapped);
}
