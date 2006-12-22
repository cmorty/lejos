package lejos.nxt;

public class SoundSensor {

	Port port;
	
	public SoundSensor(Port port)
	{
	   this.port = port;
	   port.activate(); // Default to DB
	}
	
	public SoundSensor(Port port, boolean dba)
	{
	   this.port = port;
	   if (!dba) port.activate();
	}

	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}

}
