package listener;

import environment.wrapper.ServerPlayer;

public interface IPlayerListener extends IListener {
	default void onDisconnect(ServerPlayer me) {
	};
}
