package listener;

import server.Server;


public interface IServerListener extends IListener {
	public void onTick(long _ms);
	public void onShutdown(Server _server);
}
