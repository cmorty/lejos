package lejos.nxt;

public interface PortListener 
{
	
	  /**
	   * Called when the raw value of the sensor attached to the port changes.
	   * @param aSource The Port that generated the event.
	   * @param aOldValue The old sensor raw value.
	   * @param aNewValue The new sensor raw value.
	   */
	  public void stateChanged (Port aSource, int aOldValue, int aNewValue);
}
