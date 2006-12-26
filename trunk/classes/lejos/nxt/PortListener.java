package lejos.nxt;

public interface PortListener 
{
	
	  /**
	   * Called when the canonical value of the sensor changes.
	   * @param aSource The Port that generated the event.
	   * @param aOldValue The old sensor raw value.
	   * @param aNewValue The new sensor raw value.
	   */
	  public void stateChanged (Port aSource, int aOldValue, int aNewValue);
}
