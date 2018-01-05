package util;

public class ServerMessage extends Message {
	public ServerMessage(String text) {
		super(-1, ServerConst.SERVER, text);
	}
}
