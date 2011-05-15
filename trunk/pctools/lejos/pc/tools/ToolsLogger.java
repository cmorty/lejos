package lejos.pc.tools;

import lejos.pc.comm.SystemContext;


public class ToolsLogger implements ToolsLogListener {

	public void logEvent(String message) {
		SystemContext.out.println("leJOS NXJ> " + message);
	}

	public void logEvent(Throwable throwable) {
		SystemContext.err.println("leJOS NXJ> " + throwable.getMessage());
	}

}
