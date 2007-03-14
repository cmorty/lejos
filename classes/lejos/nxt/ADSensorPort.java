package lejos.nxt;

public interface ADSensorPort extends SimpleSensorPort {

	public boolean readBooleanValue();
	
	public int readRawValue();
	
	public int readValue();
}
