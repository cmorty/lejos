package lejos.nxt;

public class RCXLightSensor {
	Port port;
	
	public RCXLightSensor(Port port)
	{
		this.port = port;
		port.setPowerType(1); // Default to LED on
	}
	
	public void activate()
	{
		port.setPowerType(1);
	}
	
	public void passivate()
	{
		port.setPowerType(0);
	}
	
	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}
}
