package tokenizer;

import java.util.ArrayList;
import java.util.List;

import util.Const;
import util.ServerConst;
import environment.entity.Entity;

public class EntityTokenizer<E extends Entity> extends Tokenizer<E> {
	public List<String> tokenize(E _ent) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN+Const.PAR_ENTITY);
		tokens.add(Const.PAR_ID+_ent.getId());
		tokens.add(Const.PAR_TYPE+_ent.getClass().getSimpleName());
		tokens.add(Const.PAR_BUSY+_ent.isBusy());
		tokens.add(Const.PAR_DESCRIPTION+_ent.getDescription());
		tokens.add(Const.PAR_XPOS+_ent.getPosition().x);
		tokens.add(Const.PAR_YPOS+_ent.getPosition().y);
		tokens.add(ServerConst.END+Const.PAR_ENTITY);
		return tokens;
	}
}
