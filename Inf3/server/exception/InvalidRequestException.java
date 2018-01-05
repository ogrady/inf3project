package exception;

import server.TcpClient;

public class InvalidRequestException extends Exception {
	private static final long serialVersionUID = 1L;
	private final TcpClient _source;
	private final String _request;

	public TcpClient getSource() {
		return this._source;
	}

	public String getRequest() {
		return _request;
	}

	public InvalidRequestException(TcpClient src, String request) {
		super("invalid request: " + request);
		_source = src;
		_request = request;
	}
}
