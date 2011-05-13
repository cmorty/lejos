package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.robotics.Accelerometer;

/**
 * Factory for I2C sensor implementations.
 * Tests what make of sensor is connected to a port and creates 
 * an instance of the appropriate class for a given sensor interface.
 * 
 * @author Lawrie Griffiths
 *
 */
public class SensorSelector {
	
	private static final String MINDSENSORS_ID = "mndsnsrs";
	private static final String HITECHNIC_ID = "HiTechnc";
		
	public static Accelerometer createAccelerometer(I2CPort port) throws SensorSelectorException {
		I2CSensor tester = new I2CSensor(port);
		String type = tester.getProductID();
		
		if (type.equals(MINDSENSORS_ID)) return new AccelMindSensor(port);
		if (type.equals(HITECHNIC_ID)) return new AccelHTSensor(port);
		
		throw new SensorSelectorException("No Such Sensor");	
	}
}
