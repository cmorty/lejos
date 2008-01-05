import lejos.nxt.*;

/**
 * Test of the Mindsensors RCX Motor Multiplexer.
 * 
 * The adapter should be connected to S1 and an RCX
 * motor connected to port A on the multiplexer.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RCXMMTest {

	public static void main(String[] args) throws Exception {
		RCXMotorMultiplexer mm = new RCXMotorMultiplexer(SensorPort.S1);
		
		while (true) {
			LCD.drawString(mm.getSensorType(), 0, 0);
			LCD.drawInt(mm.getDirection(0),3,0,1);
			LCD.drawInt(mm.getSpeed(0),3,0,2);
			LCD.refresh();
			
			mm.A.setPower(100);
			mm.A.forward();
			Thread.sleep(1000);
			mm.A.backward();
			Thread.sleep(1000);
		}
	}
}
