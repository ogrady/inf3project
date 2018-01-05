package db;

import java.io.File;

/**
 * Hook to delete files when shutting down the program. Can be used to delete
 * the database-file if running in non-persistent mode.
 * 
 * @author Daniel
 */
public class DeleteFileOnShutdownHook extends Thread {
	private String file;

	public DeleteFileOnShutdownHook(String _file) {
		this.file = _file;
	}

	@Override
	public void run() {
		try {
			System.out.println(String.format("Deleting %s: %s", file, new File(file).delete() ? "success" : "failed"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
