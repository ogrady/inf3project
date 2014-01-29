package tokenizer;

import java.util.ArrayList;
import java.util.List;

import util.Const;
import util.ServerConst;
import environment.MapCell;
import environment.Property;

public class MapCellTokenizer extends Tokenizer<MapCell> {

	@Override
	public List<String> tokenize(MapCell _mc) {
		List<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN+Const.PAR_CELL);
		tokens.add(Const.PAR_ROW+_mc.getX());
		tokens.add(Const.PAR_COLUMN+_mc.getY());
		tokens.add(ServerConst.BEGIN+Const.PAR_PROPS);
		for(Property p : _mc.getProperties()) {
			tokens.addAll(p.tokenize());
		}
		tokens.add(ServerConst.END+Const.PAR_PROPS);
		tokens.add(ServerConst.END+Const.PAR_CELL);
		return tokens;
	}

}
