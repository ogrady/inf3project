package tokenizer;

import java.util.ArrayList;
import java.util.List;

import util.Const;
import util.Message;
import util.ServerConst;

public class MessageTokenizer extends Tokenizer<Message> {

	@Override
	public List<String> tokenize(Message message) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(ServerConst.BEGIN + Const.PAR_MESSAGE);
		tokens.add(Const.PAR_SRC_ID + message.getSenderid());
		tokens.add(Const.PAR_SENDER + message.getSender());
		tokens.add(Const.PAR_TEXT + message.getText());
		tokens.add(Const.PAR_END + Const.PAR_MESSAGE);
		return tokens;
	}

}
