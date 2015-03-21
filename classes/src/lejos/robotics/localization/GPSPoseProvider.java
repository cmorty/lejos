package lejos.robotics.localization;

import javax.microedition.location.*;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.*;

/**
 * <p>This class is capable of providing a Pose estimation for a navigating robot using GPS. There
 * are a few things to keep in mind when using GPS with small robots:</p>
 * <li>GPS is not very accurate, especially for small slow-moving robots
 * <li>reported GPS coordinates tend to jump around a lot even when the robot is stationary
 * <li>GPS has problems reporting heading unless the robot has been moving in the same direction for some time
 * <li>getPose() returns coordinates in <b>centimeters only</b>
 * 
 * <p><b>NOTE: As such, this should only be used if your robot will be making moves larger than about 5 meters (500 cm),
 * such as in an empty parking lot.</b></p> 
 * 
 *  <p>The alternate GPSPoseProvider constructor is recommended, which uses a Pilot to help estimate heading:</p>
 *  <code>LocationProvider lp = LocationProvider.getInstance(null); // for Bluetooth GPS
 *		  DifferentialPilot pilot = new DifferentialPilot(TREAD_SIZE, TRACK_WIDTH, Motor.B, Motor.C, false);<br>
 *		  GPSPoseProvider pp = new GPSPoseProvider(lp, pilot);
 *  </code>
 *  
 *  <p>The orientation of the coordinate system is currently locked with true North, with the positive y-axis along the 
 *  north axis, and the positive x-axis along the east axis. 
 *  The setPose() method serves no purpose at the moment. Normally it would reset the current relative coordinates
 *  to whatever pose it is given, such as x=0, y=0, heading=0.</p>
 *  
 * @author BB
 *
 */
public class GPSPoseProvider implements PoseProvider {

	private Coordinates originCoords = null; // The global GPS coordinates that represent x=0, y=0
	private Pose originPose = null; // The corresponding originPose to the originCoordinates. Currently unused. 
	private double originHeading; // The heading at origin. Currently unused.
	private double heading = 0; // This variable keeps a running estimation of heading. Periodically updated by GPS moves.
	private LocationProvider lp = null; // the GPS
	private MoveProvider mp = null; // The pilot. Used to estimate heading.
	
	/**
	 * This method creates a basic GPSPoseProvider that is not very capable of estimating heading. It is
	 * not recommended to use this constructor if you are using this class for robot navigation.
	 * 
	 * @param lp The LocationProvider, typically a GPS.
	 */
	public GPSPoseProvider(LocationProvider lp) {
		this(lp, null);
	}
	
	/**
	 * This method creates a GPSPoseProvider that estimates heading by monitoring the pilot moves. The GPS
	 * periodically updates the heading after straight line moves. 
	 * 
	 * @param lp The LocationProvider, typically a GPS.
	 * @param mp A MoveProvider (Pilot), such as DifferentialPilot.
	 */
	public GPSPoseProvider(LocationProvider lp, MoveProvider mp) {
		this.lp = lp;
		if(mp != null) {
			ML ml = new ML();
			mp.addMoveListener(ml);
		}
		
		// set the origin by taking GPS coordinate reading when it starts
		Pose p = new Pose(0,0,(float)heading);
		setPose(p); 
	}
	
	public Pose getPose() {
		
		// 1. Get current location, c, from LocationProvider 
		Location l = null;
		try {
			l = lp.getLocation(-1);
		} catch (LocationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Coordinates c = l.getQualifiedCoordinates();
		
		// 2. Calc distance along x axis from origin
		Coordinates cx = new Coordinates(originCoords.getLatitude(), c.getLongitude());
		double xDiff = originCoords.distance(cx); 
		
		// 3. Calc distance along y axis from origin
		Coordinates cy = new Coordinates(c.getLatitude(), originCoords.getLongitude());
		double yDiff = originCoords.distance(cy); 
				
		// convert from m to cm
		xDiff *= 100;
		yDiff *= 100;
		
		// If no move provider, try to obtain heading from GPS. Note: Holux doesn't return anything.
		if(mp != null) 
			heading = l.getCourse(); // TODO: Use convertAzimuth() on heading?
		
		
		// TODO: Translate to relative coords using originPose. See setPose()
		
		return new Pose((float)xDiff, (float)yDiff, (float)heading);
	}

	public void setPose(Pose aPose) {
		// Get Coordinates for current position
		try {
			// originCoords = new Coordinates(49.864048,-97.256041); // simulated
			originCoords  = lp.getLocation(-1).getQualifiedCoordinates();
			originHeading = lp.getLocation(-1).getCourse();
		} catch (LocationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 2. Set these coordinates as aPose. Translate the coordinates elsewhere in getPose().
		originPose = aPose;
	}

	/**
	 * <p>Converts from the WGS84 azimuth produced by Coordinates.azimuthTo() into standard coordinates that
	 * increase counter-clockwise, with 0 degrees as east.</p>
	 * 
	 * @param azimuth The azimuth produced by Coordinates.azimuthTo()
	 * @return heading, in degrees of standard coordinate system
	 */
	public static double convertAzimuth(double azimuth) {
		azimuth = 360 - azimuth; // make angle increase counter-clockwise
		azimuth += 90; // adjust 90 degrees so that North = 90 degrees.
		
		// Normalize between 0 to 360:
		while (azimuth < 0) azimuth += 360;
		while(azimuth >= 360) azimuth -= 360;
		
		return azimuth;
	}
	
	/**
	 * This listener updates the heading based on the MoveProvider and resets the heading
	 * value based on GPS coordinates after a straight travel is performed.
	 *
	 */
	private class ML implements MoveListener {

		Coordinates from;
		
		public void moveStarted(Move event, MoveProvider mp) {
			if(event.getMoveType() == Move.MoveType.TRAVEL)
				try {
					from = lp.getLocation(-1).getQualifiedCoordinates();
				} catch (LocationException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}

		public void moveStopped(Move event, MoveProvider mp) {
			double angleChange = event.getAngleTurned();
			heading += angleChange;
			while(heading >= 360) heading -= 360;
			while(heading < 0) heading += 360;
			
			if(event.getMoveType() == Move.MoveType.TRAVEL)
				try {
					Coordinates to = lp.getLocation(-1).getQualifiedCoordinates();
					// TODO: Only update if travel is certain distance? Such as some factor of QualifiedCoordinates.getHorizontalAccuracy()
					// Calculate heading and reset global heading:
					heading = from.azimuthTo(to);
					heading = convertAzimuth(heading); // convert to standard coordinate system
				} catch (LocationException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
		}
		
	}
}
