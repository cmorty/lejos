import lejos.nxt.*;
import lejos.nxt.addon.*;

/**
 * Test of remote access to Acceleration (Tilt) sensor using
 * iCommand-like classes in pccomm.jar.
 * 
 * @author Lawrie Griffiths
 *
 */
public class AccelDemo {
	public static void main(String [] args) throws Exception {
		AccelMindSensor a = new AccelMindSensor(SensorPort.S1);
		//AccelHTSensor a = new AccelHTSensor(SensorPort.S1);
		
		System.out.println("Prod ID " + a.getProductID());
		System.out.println("Port Type " + a.getSensorType());
		System.out.println("Version " + a.getVersion());
				
		for(;;) {
			System.out.println("TILT: x " + a.getXTilt() + "  y " + a.getYTilt() + " z " + a.getZTilt());
			System.out.println("ACCEL: x " + a.getXAccel() + "  y " + a.getYAccel() + " z " + a.getZAccel());
		}
	}
}