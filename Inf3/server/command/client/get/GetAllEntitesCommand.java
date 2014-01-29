package command.client.get;

import java.util.Iterator;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import command.ClientCommand;

import environment.wrapper.ServerDragon;
import environment.wrapper.ServerPlayer;

public class GetAllEntitesCommand extends ClientCommand {

	public GetAllEntitesCommand(Server _server) {
		super(_server, ServerConst.GET_ENTITIES);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		int cnt = 0;
		_src.beginMessage();
		_src.send(ServerConst.BEGIN+ServerConst.ANS_ENTITIES);
		//Iterator<ServerPlayer> pit = Const.db.query(ServerPlayer.class).iterator();
		// db here
		Iterator<ServerPlayer> pit = ServerPlayer.instances.values().iterator();
		while(pit.hasNext()) {
			_src.sendTokenizable(pit.next());
			cnt++;
		}
		//Iterator<ServerDragon> dit = Const.db.query(ServerDragon.class).iterator();
		// db here
		Iterator<ServerDragon> dit = ServerDragon.instances.values().iterator();
		while(dit.hasNext()) {
			_src.sendTokenizable(dit.next());
			cnt++;
		}
		_src.send(ServerConst.END+ServerConst.ANS_ENTITIES);
		_src.endMessage();
		_mes.append(String.format("sent %d entities to %s", cnt, _src.toString()));
		return 1;
	}
}
