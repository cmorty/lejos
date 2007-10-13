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

	@Override
	public void logEvent(String message) {
		LeJOSNXJUtil.message(message);

	}

	@Override
	public void logEvent(Throwable throwable) {
		LeJOSNXJUtil.message(throwable);

	}

	@Override
	public boolean isCanceled() {
		return Thread.currentThread().isInterrupted();
	}

	@Override
	public void log(String message) {
		logEvent(message);
	}

	@Override
	public void operation(String message) {
		logEvent(message);
	}

	@Override
	public void progress(int progress) {
		String message = "\r  " + (progress / 10) + "%\r";
		logEvent(message);
	}

}
