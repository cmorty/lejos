package lejos.nxt;

public class SoundSensor {

	Port port;
	
	public SoundSensor(Port port)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType(1); // Default to DB
	}
	
	public SoundSensor(Port port, boolean dba)
	{
	   this.port = port;
	   port.setPowerType(0);
	   port.setADType((dba ? 2 : 1));
	}
	
	public void setDBA(boolean dba)
	{
		port.setADType((dba ? 2 : 1));
	}

	public int readValue()
	{
		return ((1023 - port.readRawValue()) * 100/ 1023);  
	}

}
