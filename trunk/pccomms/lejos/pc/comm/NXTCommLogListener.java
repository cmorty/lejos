package lejos.pc.comm;

/**
 * 
 * @author scholz
 * listener for log events
 */

public interface NXTCommLogListener {
	
	public void logEvent(String message);
	public void logEvent(Throwable throwable);

}
