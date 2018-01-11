package util;

public class Answer {
	public static final Answer NO = new Answer(0, Const.PAR_NO);
	public static final Answer OK = new Answer(1, Const.PAR_OK);
	public static final Answer INV = new Answer(-1, Const.PAR_INVALID);
	public static final Answer UNK = new Answer(-2, Const.PAR_UNKNOWN);

	private final int _code;
	private final String _message;

	public int getCode() {
		return _code;
	}

	public String getMessage() {
		return _message;
	}

	public Answer(int code, String val) {
		_code = code;
		_message = val;
	}
}
