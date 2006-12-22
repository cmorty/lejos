package lejos.nxt;

public class LightSensor {
	Port port;
	
	public LightSensor(Port port)
	{
		this.port = port;
		port.activate(); // Default to LED on
	}
	
	public LightSensor(Port port, boolean activate)
	{
	   this.port = port;
	   if (activate) port.activate();
	   else port.passivate();
	}

	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}
}
