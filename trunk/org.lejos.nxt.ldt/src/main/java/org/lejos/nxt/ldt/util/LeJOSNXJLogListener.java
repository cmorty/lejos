package org.lejos.nxt.ldt.util;

import js.common.ToolProgressMonitor;
import lejos.pc.tools.ToolsLogListener;

/**
 * log listener and monitor for the plugin 
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSNXJLogListener implements ToolsLogListener,
		ToolProgressMonitor {

	private boolean _verbose = false;

	public void logEvent(String message) {
		LeJOSNXJUtil.message(message);

	}

	public void logEvent(Throwable throwable) {
		LeJOSNXJUtil.message(throwable);

	}

	public boolean isCanceled() {
		return Thread.currentThread().isInterrupted();
	}

	public void log(String message) {
		if (!_verbose)
			return;
		logEvent(message);
	}

	public void operation(String message) {
		logEvent(message);
	}

	public void progress(int progress) {
		String message = "\r  " + (progress / 10) + "%\r";
		logEvent(message);
	}

	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}

}
