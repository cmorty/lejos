import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.robotics.*;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.SegwayPilot;

/**
 * <p>This class demonstrates the movement capabilities of the SegwayPilot by tracing three squares on the floor.
 * It also implements a MoveListener to demonstrate the move reporting abilities of all XxxPilot classes. 
 * As of version 0.9 the SegwayPilot.rotate() method is very poor, but we will improve this for version 1.0.</p>
 * 
 * <p>The Segway and SegwayPilot classes require a HiTechnic gyro sensor. Mount the sensor on the left side of the 
 * robot. See <a href="http://laurensvalk.com/nxt-2_0-only/anyway">this model</a> for the proper orientation.</p>
 * 
 * @author BB
 *
 */
public class SegwayPilotDemo implements MoveListener {

	public static void main(String [] args) throws InterruptedException {
		NXTMotor left = new NXTMotor(MotorPort.B);
		NXTMotor right = new NXTMotor(MotorPort.C);
		
		GyroSensor g = new GyroSensor(SensorPort.S1);
				
		// The track width is for the AnyWay. Make sure to use the appropriate wheel size.
		SegwayPilot pilot = new SegwayPilot(left, right, g, SegwayPilot.WHEEL_SIZE_NXT2, 10.45); 
		
		// If the robot is tippy, try slowing down the speed:
		pilot.setTravelSpeed(80);
		
		MoveListener listy = new SegwayPilotDemo();
		pilot.addMoveListener(listy);
		
		// Draw three squares
		for(int i=0;i<12;i++) {
			pilot.travel(50);
			pilot.rotate(90);
			Thread.sleep(2000);
		}
	}

	public void moveStarted(Move move, MoveProvider mp) {
		System.out.println("MOVE STARTED");
	}

	public void moveStopped(Move move, MoveProvider mp) {
		System.out.println("DISTANCE " + move.getDistanceTraveled());
		System.out.println("ANGLE " + move.getAngleTurned());
	}	
}
