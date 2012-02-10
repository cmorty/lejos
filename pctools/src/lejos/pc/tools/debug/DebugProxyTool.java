package lejos.pc.tools.debug;

import js.tinyvm.DebugData;
import lejos.pc.comm.NXTCommLoggable;

/**
 *
 * @author Felix Treede
 *
 */
public class DebugProxyTool extends NXTCommLoggable {
	private ThreadGroup group;

	private DebuggerListener debuggerListener;
	private NXTListener nxtListener;

	private boolean debug=true;

	public DebugProxyTool(DebugData debugData, Connection debuggerConnection, Connection nxtConnection) {
		group = new ThreadGroup("NXJDebugProxy");

		debuggerListener = new DebuggerListener(this, debuggerConnection, debugData);
		nxtListener = new NXTListener(this, nxtConnection, debugData);

		debuggerListener.nxtListener = nxtListener;
		nxtListener.debuggerListener = debuggerListener;
	}

	public void start() {
		debuggerListener.start();
		nxtListener.start();
	}

	public void waitForCompletion() throws InterruptedException {
		debuggerListener.join();
		nxtListener.join();
	}

	public void stop() {
		debuggerListener.setStop();
		nxtListener.setStop();
	}

	public boolean isRunning() {
		return group.activeCount() > 0;
	}

	public ThreadGroup getThreadGroup() {
		return group;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isDebug() {
		return debug;
	}

	// Overridden to be accessible for the other classes inside the package
	@Override
	protected void log(Throwable t) {
		super.log(t);
	}

	@Override
	protected void log(String s) {
		super.log(s);
	}

	protected void debug(String s) {
		if (debug)
			super.log(s);
	}

}
