package tokenizer;

import java.util.ArrayList;
import java.util.List;

import util.Const;
import util.ServerConst;
import environment.Map;

public class MapTokenizer extends Tokenizer<Map> {

	@Override
	public List<String> tokenize(Map _map) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN+Const.PAR_MAP);
		tokens.add(Const.PAR_WIDTH+_map.getWidth());
		tokens.add(Const.PAR_HEIGHT+_map.getWidth());
		tokens.add(ServerConst.BEGIN+Const.PAR_CELLS);
		MapCellTokenizer cellTokenizer = new MapCellTokenizer();
		for(int i = 0; i < _map.getWidth(); i++) {
			for( int j = 0; j < _map.getHeight(); j++) {
				tokens.addAll(cellTokenizer.tokenize(_map.getCellAt(i,j)));
			}
		}
		tokens.add(ServerConst.END+Const.PAR_CELLS);
		tokens.add(ServerConst.END+Const.PAR_MAP);
		return tokens;
	}

}
