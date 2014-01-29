package environment;

import java.util.List;

import server.Server;
import tokenizer.ITokenizable;
import tokenizer.MapCellTokenizer;
import environment.wrapper.ServerWrapper;

public class ServerMapCell extends ServerWrapper<MapCell> implements ITokenizable {
	private MapCellTokenizer tokenizer;
	
	public ServerMapCell(MapCell _mc, Server _server) {
		super(_mc, _server);
		tokenizer = new MapCellTokenizer();
	}

	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(wrapped);
	}
}