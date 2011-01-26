import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.NXTLineLeader;
import lejos.nxt.addon.NXTLineLeader.LineColor;

/**
 * 
 * An Example to show how to use NXTLineLeader with 2 NXT Motors.
 * The results are fantastic if you compare this sensor in compare to one light sensor 
 * to create the classic line follower robot
 * 
 * @author Eric Pascual
 *
 */
public class NXTLineLeaderTest {
	NXTLineLeader ll = new NXTLineLeader(SensorPort.S1);
    NXTMotor MA = new NXTMotor(MotorPort.A);
    NXTMotor MC = new NXTMotor(MotorPort.C);

	void go() {
		LCD.clear();
		LCD.drawString("MindSensor", 0, 0) ;
		LCD.drawString("LineLeader " + ll.getVersion(), 0, 1);
		LCD.drawString("Demo by EP", 0, 2);
		
		ll.wakeUp();
		
		LCD.drawString("Calibrate white", 0, 5);
		Button.waitForPress();
		ll.calibrate(NXTLineLeader.LineColor.WHITE) ;

		LCD.drawString("Calibrate black", 0, 5);
		Button.waitForPress();
		ll.calibrate(NXTLineLeader.LineColor.BLACK) ;

		LCD.clear();
		LCD.drawString("Ready", 0, 0);
		Button.waitForPress();

		LCD.clear();
		LCD.drawString("Running...", 0, 0);

		MA.forward();
		MC.forward();

		int lastResult = -1;
		int lastSteering = 0;
		do {
			int llResult = ll.getResult();
			if (lastResult != llResult) {
				switch (llResult) {
				case 0xff:
					// LCD.drawString("ALL BLACK", 0, 3);
					MA.stop();
					MC.stop();
					break;
				case 0x00:
					// LCD.drawString("ALL WHITE", 0, 3);
					MA.setPower(20 - lastSteering);
					MC.setPower(20 + lastSteering);
					MA.forward();
					MC.forward();
					break;
				default:
					int steering = ll.getSteering();
					int power = 100 - Math.abs(steering);
					MA.setPower(power - steering);
					MC.setPower(power + steering);
					MA.forward();
					MC.forward();
					lastSteering = steering;
				}
				lastResult = llResult;
			}
		} while (Button.readButtons() == 0);

		ll.sleep();
	}

	public static void main(String[] args) {
		new NXTLineLeaderTest().go();
	}
}

