package command;

public class TopLevelCommand<S> extends Command<S> {
	public TopLevelCommand() {
		super("");
	}

	@Override
	public int execute(S src, String cmd, StringBuilder mes) {
		return executeSubcommands(src, cmd, mes);
	}

	@Override
	protected int routine(S src, String cmd, StringBuilder mes) {
		return 0;
	}
}
