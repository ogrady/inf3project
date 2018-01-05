package tokenizer;

import java.util.ArrayList;
import java.util.List;

import util.Const;
import util.ServerConst;
import environment.entity.Entity;

public class EntityTokenizer<E extends Entity> extends Tokenizer<E> {
	@Override
	public List<String> tokenize(E ent) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN + Const.PAR_ENTITY);
		tokens.add(Const.PAR_ID + ent.getId());
		tokens.add(Const.PAR_TYPE + ent.getClass().getSimpleName());
		tokens.add(Const.PAR_BUSY + ent.isBusy());
		tokens.add(Const.PAR_DESCRIPTION + ent.getDescription());
		tokens.add(Const.PAR_XPOS + ent.getPosition().x);
		tokens.add(Const.PAR_YPOS + ent.getPosition().y);
		tokens.add(ServerConst.END + Const.PAR_ENTITY);
		return tokens;
	}
}
