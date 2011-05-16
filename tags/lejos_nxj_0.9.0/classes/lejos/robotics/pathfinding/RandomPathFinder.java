package lejos.robotics.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.WayPoint;
import lejos.robotics.navigation.WayPointListener;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * PathFinder that takes a map and a dummy set of range readings.
 * It finds a path that is in short moves, has no obstacles in the
 * way and where valid range readings can be taken from each waypoint.
 * 
 * The algorithm  is not deterministic so each time it is called a new route 
 * will be found.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RandomPathFinder implements PathFinder {	
	private static final long serialVersionUID = 1L;
	private static final int MAX_ITERATIONS = 1000;
	private static final float MAX_DISTANCE = 40;
	private static final float MIN_GAIN = 10;
    private static final float MAX_RANGE = 100;	
	private static int BORDER = 20;
	
	private RangeMap map;;
	private RangeReadings readings;
	
	private ArrayList<WayPointListener> listeners ;
	
	public RandomPathFinder(RangeMap map, RangeReadings readings) {
		this.map = map;
		this.readings = readings;
	}
	
	public Collection<WayPoint> findRoute(Pose start, WayPoint destination)
			throws DestinationUnreachableException {
		Pose pose = start;
		ArrayList<WayPoint> route = new ArrayList<WayPoint>();
		
		// Continue until we return a route or throw DestinationUnReachableException
		for(;;) {
			// If the current pose if close enough to the destination, go straight there
			if (pose.distanceTo(destination) < MAX_DISTANCE) {
				route.add(new WayPoint(destination));
				return route;
			} else {
				Pose testPose = null;
				
				// Generate random poses and apply tests to them
				for(int i=0;i<MAX_ITERATIONS;i++) {
				    testPose = generatePose();
				    
				    // The new Pose must not be more than MAX_DISTANCE away from current pose	    
				    if (testPose.distanceTo(pose.getLocation()) > MAX_DISTANCE) continue;
				    
					// The new pose must be at least MIN_GAIN closer to the destination
					if (pose.distanceTo(destination) - 
							testPose.distanceTo(destination) < MIN_GAIN)
						continue;
					
					// We must be able to get a valid set of range readings from the new pose
					float heading = testPose.getHeading();
					boolean validReadings = true;
					for(RangeReading r: readings) {
						testPose.setHeading(heading + r.getAngle());
						float range = map.range(testPose);
						if (range > MAX_RANGE) {
							validReadings = false;
							break;
						}
					}					
					if (!validReadings) continue;
					
					//Check there are no obstacles in the way 
					testPose.setHeading(testPose.angleTo(pose.getLocation()));
					if (map.range(testPose) < testPose.distanceTo(pose.getLocation()))
						continue;					
					
					testPose.setHeading(heading); // Restore heading
					break; // We have a new way point
				}
				if (testPose == null) throw new  DestinationUnreachableException();
				else {
					route.add(new WayPoint(testPose));
					pose = testPose;
				}
			}
		}
	}
	
	/**
	 * Generate a random pose within the mapped area, not too close to the edge
	 */
	private Pose generatePose() {
	    float x, y, heading;
	    Rectangle boundingRect = map.getBoundingRect();
	    Rectangle innerRect = new Rectangle(boundingRect.x + BORDER, boundingRect.y + BORDER,
	        boundingRect.width - BORDER * 2, boundingRect.height - BORDER * 2);

	    // Generate x, y values in bounding rectangle
	    for (;;) { // infinite loop that we break out of when we have
	               // generated a particle within the mapped area
	      x = innerRect.x + (((float) Math.random()) * innerRect.width);
	      y = innerRect.y + (((float) Math.random()) * innerRect.height);

	      if (map.inside(new Point(x, y))) break;
	    }

	    // Pick a random angle
	    heading = ((float) Math.random()) * 360;
	    
	    return new Pose(x,y,heading);
	}
	
	public void addListener(WayPointListener wpl) {
	    if(listeners == null )listeners = new ArrayList<WayPointListener>();
	    listeners.add(wpl);
	  }

	  public void startPathFinding(Pose start, WayPoint end) {
		  Collection<WayPoint> solution = null;
		try {
			solution = findRoute(start, end);
		} catch (DestinationUnreachableException e) {
			// TODO Not sure what the proper response is here. All in one.
			
		}
		  if(listeners != null) { 
			  for(WayPointListener l : listeners) {
				  Iterator<WayPoint> iterator = solution.iterator(); 
				  while(iterator.hasNext()) {
					  l.nextWaypoint(iterator.next());
				  }
				  l.pathComplete();
			  }
		  }
	  }
}
