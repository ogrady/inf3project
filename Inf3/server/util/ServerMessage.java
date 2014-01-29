package util;

public class ServerMessage extends Message {
	public ServerMessage(String _text) {
		super(-1, ServerConst.SERVER, _text);
	}
}
