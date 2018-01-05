package db;

public class DeleteDBOnShutdown extends DeleteFileOnShutdownHook {

	public DeleteDBOnShutdown(String _file) {
		super(_file);
	}

	@Override
	public void run() {
		// db here
		// Const.db.close();
		super.run();
	}
}
