package tokenizer;

import java.util.List;

import util.Const;
import util.ServerConst;
import environment.entity.Dragon;

public class DragonTokenizer extends EntityTokenizer<Dragon> {
	@Override
	public List<String> tokenize(Dragon _dr) {
		List<String> tokens = super.tokenize(_dr);
		tokens.set(0, ServerConst.BEGIN+Const.PAR_DRAGON);
		tokens.set(tokens.size()-1, ServerConst.END+Const.PAR_DRAGON);
		return tokens;
	}
}
