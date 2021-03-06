package util;

/**
 * A textmessage send over the server
 * 
 * @author Daniel
 */
public class Message {
	private final int senderid;
	private final String sender;
	private final String text;

	public int getSenderid() {
		return senderid;
	}

	public String getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	/**
	 * Constructor
	 * 
	 * @param _senderid
	 *            id of the sending entity. -1 for server messages
	 * @param _sender
	 *            name of the sender for convenience
	 * @param _text
	 *            textmessage
	 */
	public Message(int _senderid, String _sender, String _text) {
		senderid = _senderid;
		sender = _sender;
		text = _text;
	}

	@Override
	public String toString() {
		return String.format("%1$s: %2$s", sender, text);
	}
}
