package exception;

import server.TcpClient;

public class InvalidRequestException extends Exception {
	private static final long serialVersionUID = 1L;
	private TcpClient source;
	private String request;
	
	public TcpClient getSource() { return this.source; }	
	public String getRequest() { return request; }
	
	public InvalidRequestException(TcpClient _src, String _request) {
		super("invalid request: "+_request);
		source = _src;		
		request = _request;
	}
}
