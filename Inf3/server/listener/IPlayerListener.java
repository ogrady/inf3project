package listener;

import environment.wrapper.ServerPlayer;

public interface IPlayerListener extends IListener {
	void onDisconnect(ServerPlayer _me);
}
