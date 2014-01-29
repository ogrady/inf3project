package command;

public class TopLevelCommand<S> extends Command<S> {
	public TopLevelCommand() {
		super("");
	}

	@Override
	public int execute(S _src, String _cmd, StringBuilder _mes) {
		return executeSubcommands(_src, _cmd, _mes);
	}

	@Override
	protected int routine(S _src, String _cmd, StringBuilder _mes) {
		return 0;
	}
}
