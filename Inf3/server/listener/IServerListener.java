package listener;

import server.Server;

public interface IServerListener extends IListener {
	default public void onTick(long ms) {
	};

	default public void onShutdown(Server server) {
	};
}
