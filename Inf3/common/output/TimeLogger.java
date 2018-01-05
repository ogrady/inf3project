package output;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeLogger extends Logger {
	private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

	@Override
	public void println(String str, MessageType mt) {
		print(format.format(new Date()) + ": " + str + "\r\n", mt);
	}
}
