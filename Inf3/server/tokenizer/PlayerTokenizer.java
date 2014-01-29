package tokenizer;

import java.util.List;

import util.Const;
import util.ServerConst;
import environment.entity.Player;

public class PlayerTokenizer extends EntityTokenizer<Player> {
	@Override
	public List<String> tokenize(Player _pl) {
		List<String> tokens = super.tokenize(_pl);
		// remove end tag from entity
		tokens.remove(tokens.size()-1);
		// overwrite start tag from entity
		tokens.set(0,(ServerConst.BEGIN+Const.PAR_PLAYER));
		// append player specific data
		tokens.add(Const.PAR_POINTS+_pl.getPoints());
		tokens.add(ServerConst.END+Const.PAR_PLAYER);
		return tokens;
	}
}