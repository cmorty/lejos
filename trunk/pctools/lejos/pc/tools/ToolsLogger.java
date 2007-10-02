package lejos.pc.tools;

public class ToolsLogger implements ToolsLogListener {

	@Override
	public void logEvent(String message) {
		System.out.println("leJOS NXJ> " + message);
	}

	@Override
	public void logEvent(Throwable throwable) {
		System.err.println("leJOS NXJ> " + throwable.getMessage());
	}

}
