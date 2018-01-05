package environment;

import java.util.List;

import environment.wrapper.ServerWrapper;
import server.Server;
import tokenizer.ITokenizable;
import tokenizer.MapCellTokenizer;

public class ServerMapCell extends ServerWrapper<MapCell> implements ITokenizable {
	private final MapCellTokenizer tokenizer;

	public ServerMapCell(MapCell mc, Server server) {
		super(mc, server);
		tokenizer = new MapCellTokenizer();
	}

	@Override
	public List<String> tokenize() {
		return tokenizer.tokenize(_wrapped);
	}
}