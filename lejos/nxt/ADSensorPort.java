package lejos.nxt;

public interface ADSensorPort extends BasicSensorPort {

	public boolean readBooleanValue();
	
	public int readRawValue();
	
	public int readValue();
}
