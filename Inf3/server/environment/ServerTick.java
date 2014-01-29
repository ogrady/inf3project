package environment;

import java.util.Iterator;

import listener.IServerListener;
import server.Server;
import util.Configuration;

/**
 * Ticks and calls tick() on all {@link ServerEntity}s every DELAY milliseconds
 * @author Daniel
 */
public class ServerTick implements Runnable {
	private Server server;
	private long lasttick;
	public volatile boolean running;
	
	/**
	 * Constructor
	 * @param _s instance of the running server
	 */
	public ServerTick(Server _s) {
		this.server = _s;
	}

	/**
	 * Sleeps for a period of time (specified in the config as TICK_DELAY) and then sends the passed milliseconds
	 * since it last went to sleep.<br>
	 * This doesn't have to equal the delay from sleeping but could also add some processing delay.<br>
	 * So each object will be notified of at least TICK_DELAY milliseconds but there could also be some more to it.
	 */
	@Override
	public void run() {
		running = true;
		lasttick = System.currentTimeMillis();
		while(running) {
			try {
				Thread.sleep(Configuration.getInstance().getLong(Configuration.TICK_DELAY));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Iterator<IServerListener> it = this.server.getListeners().iterator();
			while(it.hasNext()) {
				it.next().onTick(System.currentTimeMillis() - lasttick);
			}
		}
		
	}

}
