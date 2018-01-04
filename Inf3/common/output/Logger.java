package output;

import java.io.PrintStream;
import java.util.HashMap;

import org.fusesource.jansi.AnsiConsole;

import util.Bitmask;

/**
 * Logger that acts like an outputstream.
 * But it displays different information with different color
 * and can be modified in other ways.
 * It holds a {@link Bitmask} to distinguish between message that should or should not be displayed.
 * @author Daniel
 */
public class Logger {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	private static final HashMap<MessageType, String> colors = new HashMap<MessageType, String>();
	static {
		AnsiConsole.systemInstall();
		colors.put(MessageType.GENERIC, null);
		colors.put(MessageType.NOTIFICATION, ANSI_PURPLE);
		colors.put(MessageType.INFO, ANSI_YELLOW);
		colors.put(MessageType.ERROR, ANSI_RED);
	}
	private PrintStream out;
	private Bitmask mask;
	
	public PrintStream getStream() {
		return out;
	}
	
	/**
	 * Constructor
	 * @param _out outstream we want to print to. Can be a filestream or anything else.
	 */
	public Logger(PrintStream _out) {
		this.mask = new Bitmask();
		this.out = _out;
	}
	
	/**
	 * Constructor
	 * by default the AnsiConsole is the outstream
	 */
	public Logger() {
		this(AnsiConsole.out);
	}
	
	/**
	 * Accept messages of a certain type
	 * @param _mt type of messages we want to print to the stream
	 */
	public void accept(MessageType... _mt) {
		for(MessageType t : _mt) {
			mask.add((int) Math.pow(2, t.ordinal()));
		}
	}
	
	/**
	 * Dismiss messages of a certain type
	 * @param _mt type of messages we don't want to print to the stream
	 */
	public void dismiss(MessageType... _mt) {
		for(MessageType t : _mt) {
			mask.remove((int) Math.pow(2, t.ordinal()));
		}
	}
	
	/**
	 * Print a message to the output. It will then be flushed.
	 * If the messagetype is linked to a color, that color will be applied to the message.
	 * @param _str message
	 * @param _mt messagetype
	 */
	public void print(String _str, MessageType _mt) {
		if(mask.has((int) Math.pow(2, _mt.ordinal()))) {
			String col = colors.get(_mt);
			if(col != null) _str = col+_str;
			out.print((_str+ANSI_RESET));
			out.flush();
		}
	}
	
	/**
	 * Print a message of the given message type if accepted
	 * @param _str message
	 * @param _mt messagetype
	 */
	public void println(String _str, MessageType _mt) {
		print(_str+"\r\n", _mt);
	}
	
	/**
	 * Print a message to the stream as GENERIC message and a linebreak (convenience method)
	 * @param _str message
	 */
	public void println(String _str) {
		println(_str, MessageType.GENERIC);
	}
	
	/**
	 * Util method to print arbitary bits of text to the stream without flushing
	 * (for example ansi color codes)
	 * @param _str string bit
	 * @param _flush whether to flush or not
	 */
	private void printBit(String _str, boolean _flush) {
		out.print(_str);
		if(_flush) {
			out.flush();
		}
	}
	
	/**
	 * Prints an {@link Exception} to the stream (in red)
	 * @param _ex {@link Exception} to print
	 */
	public void printException(Exception _ex) {
		printBit(ANSI_RED,false);
		_ex.printStackTrace(out);
		printBit(ANSI_RESET,true);
	}
	
	public enum MessageType {
		GENERIC, NOTIFICATION, INFO, ERROR, DEBUG, INPUT, OUTPUT
	}
}
