package environment;

import environment.wrapper.ServerWrapper;
import server.Server;

public class ServerMapCell extends ServerWrapper<MapCell>  {

	public ServerMapCell(MapCell mc, Server server) {
		super(mc, server);
	}
}