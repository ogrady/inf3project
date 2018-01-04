package command;

import java.util.HashMap;

import util.ServerConst;

/**
 * Command class, which can execute a given command string.
 * Each command can have subcommands of the same type to execute. You can add subcommands to a command by
 * calling the {@link #addSubcommand(Command)} method. When the command receives an incoming request it will do some
 * preprocessing (strip off prefixes...). If the request is scalar (the current command can directly process it)
 * if will try to do so. If not, it will attempt to find a responsible command amongst its own subcommands.
 * Those subcommands are stored in a hashmap, where every prefix is mapped on a specific command.
 * For example:
 * ask:say:hello
 * Will reach the ASK-command. It will strip off "ask:" (as this is its own prefix) and as the request is not scalar, it will pass
 * ot on to the subcommands. "say:" will lead to the the SAY-command (through the hashmap of ASK). SAY will strip off its own prefix ("say:") and
 * recognize the remaineder ("hello") as scalar value (no more separators and it doesn't have any subcommands). It will therefore start to process it.
 * The execution of a command can have 3 results:
 * <0: the command found itself responsible the request as valid but could not execute the command (exceptions etc)
 * =0: the command is not responsible for the request 
 * >0: the command found itself resonsible for the request and executed it successfully
 * So when building a new command it is crucial that you take care of proper return values of the {@link #execute(Object, String, StringBuilder)} method
 * as those will imply when a command can stop iterating through the subcommands (<=0). 
 * @author Daniel
 */
abstract public class Command<S> {
	public static final int 
	NOT_RESPONSIBLE = 0,
	PROCESSED = 1,
	EXCEPTION = -1;
	
	protected HashMap<String, Command<S>> subcommands;
	protected String sensitive;
	
	public String getSensitive() { return sensitive; }
	public HashMap<String, Command<S>> getSubCommands() { return subcommands; }
	
	/**
	 * Evaluates the command string and executes it if possible
	 * @param _src source of the command
	 * @param _cmd command string
	 * @param _mes message that can be altered by reference
	 * @return tristatelogic: positive: successfully executed, 0: command not recognized, negative: could not execute
	 */
	public int execute(S _src, String _cmd, StringBuilder _mes) {
		int result = 0;
		if(_cmd.startsWith(sensitive)) {
			_cmd = _cmd.substring(sensitive.length());
			result = routine(_src, _cmd, _mes); 
		}
		return result;
	}
	
	/**
	 * Tries to propagate the command down to the subcommands
	 * @param _src source of the command
	 * @param _cmd command string
	 * @param _mes message that can be altered by reference. If we called this method on a scalar command (no separator in the string) an error will be appended
	 * @return tristatelogic: positive: successfully executed, 0: command not recognized, negative: could not execute 
	 */
	protected int executeSubcommands(S _src, String _cmd, StringBuilder _mes) {
		int executed = 0;
		String[] cmdtokens = _cmd.split(ServerConst.CMD_SEPARATOR);
		if(cmdtokens.length < 1) {
			_mes.append(String.format("Invalid execution of subcommands from '%s' on scalar command '%s'\r\n", getClass().getSimpleName(), _cmd));
		}
		else {
			Command<S> responsible = subcommands.get(cmdtokens[0]);
			if(responsible != null) {
				executed = responsible.execute(_src, _cmd, _mes);
			} else {
				_mes.append(String.format("%s did not recognize command '%s'\r\n",getClass().getSimpleName(),_cmd));
			}
		}
		return executed;
	}
	
	/**
	 * Add a new subcommand
	 * @param _subcommand new subcommand
	 */
	public void addSubcommand(Command<S> _subcommand) {
		//cut of the separator if any as this interferes with the split when checking the prefix
		subcommands.put(_subcommand.sensitive.split(ServerConst.CMD_SEPARATOR)[0], _subcommand);
	}
	
	/**
	 * Removes a subcommand
	 * @param _subcommand subcommand to remove
	 */
	public void removeSubcommand(Command<S> _subcommand) {
		subcommands.remove(_subcommand.sensitive.split(ServerConst.CMD_SEPARATOR)[0]);
	}
	
	/**
	 * Constructor
	 * @param _sensitive prefix this command is sensitive for
	 */
	public Command(String _sensitive) {
		subcommands = new HashMap<String, Command<S>>();
		sensitive = _sensitive;
	}
	
	/**
	 * This method executes the actual routine of the command.
	 * It is called after the command detected that it is responsible for the entered command.
	 * That is when the command starts with the prefix {@link #sensitive}.
	 * {@link #execute(Object, String, StringBuilder)} will then cut off that prefix and call this routine.
	 * If it is not responsible, {@link #execute(Object, String, StringBuilder)} will return 0 and NOT call this method.
	 * @param _src source of the command
	 * @param _cmd command string
	 * @param _mes message that can be altered by reference. If we called this method on a scalar command (no separator in the string) an error will be appended
	 * @return tristatelogic: positive: successfully executed, 0: command not recognized, negative: could not execute 
	 */
	abstract protected int routine(S _src, String _cmd, StringBuilder _mes); 
	
	@Override
	public String toString() {
		String ret = sensitive+"\r\n";
		for(String subKey : subcommands.keySet()) {
			ret += String.format("%s%s\r\n",sensitive,subcommands.get(subKey).toString());
		}
		return ret.trim();
	}
}
