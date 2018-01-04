package command.client.get;

import server.TcpClient;
import server.Server;
import util.ServerConst;

import java.util.HashMap;
import java.util.Map;

import command.ClientCommand;

public class GetServertimeCommand extends ClientCommand {

	public GetServertimeCommand(Server _server) {
		super(_server, ServerConst.GET_TIME);
	}

	@Override
	protected int routine(TcpClient _src, String _cmd, StringBuilder _mes) {
		Map<String,Long> mes = new HashMap<>();
		mes.put(ServerConst.ANS_TIME, System.currentTimeMillis());
		_src.send(server.json(mes).get());
		/*
		_src.beginMessage();
		_src.send(ServerConst.BEGIN+ServerConst.ANS_TIME);
		_src.send(""+System.currentTimeMillis());
		_src.send(ServerConst.END+ServerConst.ANS_TIME);
		_src.endMessage();
		*/
		_mes.append("sent servertime to "+_src);
		return 1;
	}
}
