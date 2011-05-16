import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.*;
import lejos.util.PilotProps;
import lejos.nxt.*;
import java.io.IOException;

/**
 * The WaypointNav class uses the NavPathController to execute the
 * individual segments
 *
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Dirk Sturzebecher - 20090131 - initial version
 * revised 20090701 to use instead of extend  a SimpleNavigator.
 * revised 20110428 to use NavPathController by BB. 
 */
public class WaypointNav
{
	NavPathController nav;

	/**
	 * test of WaypointNav
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		PilotProps pp = new PilotProps();
		pp.loadPersistentValues();
		float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.32"));
		float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.35"));
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));

		System.out.println("Any button to start");
		Button.waitForPress();
		
		DifferentialPilot p = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		NavPathController nav = new NavPathController(p);

		nav.addWayPoint(new WayPoint(20, 20));
		nav.addWayPoint(new WayPoint(20,0));
		nav.addWayPoint(new WayPoint(0,20));
		nav.addWayPoint(new WayPoint(0,0));
		System.out.println("Any button to halt");
		Button.waitForPress();
	}
}
