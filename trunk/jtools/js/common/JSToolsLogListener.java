package js.common;

/**
 * 
 * @author scholz
 * listener for log events
 */

public interface JSToolsLogListener {
	
	public void logEvent(String message);
	public void logEvent(Throwable throwable);

}
