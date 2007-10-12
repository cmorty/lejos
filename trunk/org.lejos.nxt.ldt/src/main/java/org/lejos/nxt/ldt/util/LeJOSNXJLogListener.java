package org.lejos.nxt.ldt.util;

import lejos.pc.tools.ToolsLogListener;

public class LeJOSNXJLogListener implements ToolsLogListener {

	@Override
	public void logEvent(String message) {
		LeJOSNXJUtil.message(message);
		
	}

	@Override
	public void logEvent(Throwable throwable) {
		LeJOSNXJUtil.message(throwable);
		
	}

}
