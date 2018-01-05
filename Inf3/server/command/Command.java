package command;

import java.util.HashMap;

import util.ServerConst;

/**
 * Command class, which can execute a given command string. Each command can
 * have subcommands of the same type to execute. You can add subcommands to a
 * command by calling the {@link #addSubcommand(Command)} method. When the
 * command receives an incoming request it will do some preprocessing (strip off
 * prefixes...). If the request is scalar (the current command can directly
 * process it) if will try to do so. If not, it will attempt to find a
 * responsible command amongst its own subcommands. Those subcommands are stored
 * in a hashmap, where every prefix is mapped on a specific command. For
 * example: ask:say:hello Will reach the ASK-command. It will strip off "ask:"
 * (as this is its own prefix) and as the request is not scalar, it will pass ot
 * on to the subcommands. "say:" will lead to the the SAY-command (through the
 * hashmap of ASK). SAY will strip off its own prefix ("say:") and recognize the
 * remaineder ("hello") as scalar value (no more separators and it doesn't have
 * any subcommands). It will therefore start to process it. The execution of a
 * command can have 3 results: <0: the command found itself responsible the
 * request as valid but could not execute the command (exceptions etc) =0: the
 * command is not responsible for the request >0: the command found itself
 * resonsible for the request and executed it successfully So when building a
 * new command it is crucial that you take care of proper return values of the
 * {@link #execute(Object, String, StringBuilder)} method as those will imply
 * when a command can stop iterating through the subcommands (<=0).
 * 
 * @author Daniel
 */
abstract public class Command<S> {
	public static final int NOT_RESPONSIBLE = 0, PROCESSED = 1, EXCEPTION = -1;

	protected HashMap<String, Command<S>> _subcommands;
	protected String _sensitive;

	public String getSensitive() {
		return _sensitive;
	}

	public HashMap<String, Command<S>> getSubCommands() {
		return _subcommands;
	}

	/**
	 * Evaluates the command string and executes it if possible
	 * 
	 * @param src
	 *            source of the command
	 * @param cmd
	 *            command string
	 * @param mes
	 *            message that can be altered by reference
	 * @return tristatelogic: positive: successfully executed, 0: command not
	 *         recognized, negative: could not execute
	 */
	public int execute(S src, String cmd, StringBuilder mes) {
		int result = 0;
		if (cmd.startsWith(_sensitive)) {
			cmd = cmd.substring(_sensitive.length());
			result = routine(src, cmd, mes);
		}
		return result;
	}

	/**
	 * Tries to propagate the command down to the subcommands
	 * 
	 * @param src
	 *            source of the command
	 * @param cmd
	 *            command string
	 * @param mes
	 *            message that can be altered by reference. If we called this method
	 *            on a scalar command (no separator in the string) an error will be
	 *            appended
	 * @return tristatelogic: positive: successfully executed, 0: command not
	 *         recognized, negative: could not execute
	 */
	protected int executeSubcommands(S src, String cmd, StringBuilder mes) {
		int executed = 0;
		final String[] cmdtokens = cmd.split(ServerConst.CMD_SEPARATOR);
		if (cmdtokens.length < 1) {
			mes.append(String.format("Invalid execution of subcommands from '%s' on scalar command '%s'\r\n",
					getClass().getSimpleName(), cmd));
		} else {
			final Command<S> responsible = _subcommands.get(cmdtokens[0]);
			if (responsible != null) {
				executed = responsible.execute(src, cmd, mes);
			} else {
				mes.append(String.format("%s did not recognize command '%s'\r\n", getClass().getSimpleName(), cmd));
			}
		}
		return executed;
	}

	/**
	 * Add a new subcommand
	 * 
	 * @param subcommand
	 *            new subcommand
	 */
	public void addSubcommand(Command<S> subcommand) {
		// cut of the separator if any as this interferes with the split when checking
		// the prefix
		_subcommands.put(subcommand.getSensitive().split(ServerConst.CMD_SEPARATOR)[0], subcommand);
	}

	/**
	 * Removes a subcommand
	 * 
	 * @param subcommand
	 *            subcommand to remove
	 */
	public void removeSubcommand(Command<S> subcommand) {
		_subcommands.remove(subcommand.getSensitive().split(ServerConst.CMD_SEPARATOR)[0]);
	}

	/**
	 * Constructor
	 * 
	 * @param _sensitive
	 *            prefix this command is sensitive for
	 */
	public Command(String sensitive) {
		_subcommands = new HashMap<>();
		_sensitive = sensitive;
	}

	/**
	 * This method executes the actual routine of the command. It is called after
	 * the command detected that it is responsible for the entered command. That is
	 * when the command starts with the prefix {@link #_sensitive}.
	 * {@link #execute(Object, String, StringBuilder)} will then cut off that prefix
	 * and call this routine. If it is not responsible,
	 * {@link #execute(Object, String, StringBuilder)} will return 0 and NOT call
	 * this method.
	 * 
	 * @param src
	 *            source of the command
	 * @param cmd
	 *            command string
	 * @param mes
	 *            message that can be altered by reference. If we called this method
	 *            on a scalar command (no separator in the string) an error will be
	 *            appended
	 * @return tristatelogic: positive: successfully executed, 0: command not
	 *         recognized, negative: could not execute
	 */
	abstract protected int routine(S src, String cmd, StringBuilder mes);

	@Override
	public String toString() {
		String ret = _sensitive + "\r\n";
		for (final String subKey : _subcommands.keySet()) {
			ret += String.format("%s%s\r\n", _sensitive, _subcommands.get(subKey).toString());
		}
		return ret.trim();
	}
}
