package lejos.nxt;

public class LightSensor {
	Port port;
	
	public LightSensor(Port port)
	{
		this.port = port;
		port.setPowerType(0);
		port.setADType(1); // Default to LED on
	}
	
	public LightSensor(Port port, boolean floodlight)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType((floodlight ? 1 : 0));
	}
	
	public void setFloodlight(boolean floodlight)
	{
		port.setADType((floodlight ? 1 : 0));
	}

	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}
}
