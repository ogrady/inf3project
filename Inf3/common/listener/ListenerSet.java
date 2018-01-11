package listener;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenerSet<L extends IListener> extends CopyOnWriteArrayList<L> {
	private static final long serialVersionUID = 1L;

	public void registerListener(L listener) {
		if (!this.contains(listener)) {
			this.add(listener);
		}
	}

	public void unregisterListener(L listener) {
		this.remove(listener);
	}

	public void unregisterAll() {
		this.clear();
	}
	
	public void alert(Consumer<L> consumer) {
		Iterator<L> it = this.iterator();
		while (it.hasNext()) {
			consumer.accept(it.next());
		}
	}

	public void notify(INotifier<L> notificator) {
		Iterator<L> it = this.iterator();
		while (it.hasNext()) {
			notificator.notify(it.next());
		}
	}
}
