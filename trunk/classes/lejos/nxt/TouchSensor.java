package lejos.nxt;

public class TouchSensor {
	Port port;
	
	public TouchSensor(Port port)
	{
	   this.port = port;
	}
	
	public boolean isPressed()
	{
		return (port.readRawValue() < 600);  
	}

}
