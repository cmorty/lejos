package lejos.nxt;

interface ADSensorPort extends SimpleSensorPort {

	public boolean readBooleanValue();
	
	public int readRawValue();
	
	public int readValue();
}
