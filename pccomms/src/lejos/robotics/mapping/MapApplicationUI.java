package lejos.robotics.mapping;

import lejos.robotics.mapping.NavigationModel.NavEvent;

public interface MapApplicationUI {
	
	/**
	 * Log a progress message
	 * 
	 * @param message the message
	 */
	public void log(String message);
	
	/**
	 * Display an error message
	 * 
	 * @param message the message
	 */
	public void error(String message);
	
	/**
	 * Fatal error which stops the application
	 * 
	 * @param message the message
	 */
	public void fatal(String message);
	
	/**
	 * Indicates that model data has changed that requires a repaint of the map panel
	 */
	public void repaint();
	
	/**
	 * Signal that an event has been received (normally from the NXT).
	 * 
	 * @param navEvent the navigation event
	 */
	public void eventReceived(NavEvent navEvent);
	
	/**
	 * Signal that the NXT is now connected
	 */
	public void whenConnected();
	
}
