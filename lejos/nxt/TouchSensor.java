package lejos.nxt;

public class TouchSensor {
	Port port;
	
	public TouchSensor(Port port)
	{
	   this.port = port;
	   port.setPowerType(0);
	}
	
	public boolean isPressed()
	{
		return (port.readRawValue() < 600);  
	}

}
