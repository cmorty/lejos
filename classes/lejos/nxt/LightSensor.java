package lejos.nxt;

public class LightSensor {
	Port port;
	
	public LightSensor(Port port)
	{
	   this.port = port;
	}
	
	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}
}
