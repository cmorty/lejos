package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;

/**
 * A PoseProvider that keeps track of coordinates using dead reckoning, by monitoring Pilot movements.
 * 
 * Question: What about a robot that is helped along with a Compass? Should the compass go in a DeadReckoner
 *  constructor, or a Pilot constructor? The PoseProvider seems more logical but Lawrie wants it to only
 *  accept a Pilot. 
 * 
 * Provisional name: DeadReckonerPoseProvider
 * Alternate names:  DeadReckonerPoseProvider, DeadReckoner, OrienteeringPoseProvider, OdometryPoseProvider 
 * 
 */
public class DeadReckonerPoseProvider implements MoveListener, PoseProvider {
	private float x = 0, y = 0, heading = 0;
	
	/**
	 * Internally, the constructor  listens to movements from the Pilot. This allows it to keep
	 * track of all vector movements made.
	 * 
	 * @param pilot
	 */
	public DeadReckonerPoseProvider(MovementProvider mp) {
		mp.addMoveListener(this);
	}
		
	public Pose getPose() {
		return new Pose(x,y,heading);
	}

	public void movementStarted(Movement event, MovementProvider mp) {		
	}

	public void movementStopped(Movement event, MovementProvider mp) {
		float angle = event.getAngleTurned();
		float distance = event.getDistanceTraveled();
        double dx = 0, dy = 0;
        double headingRad = (Math.toRadians(heading));
        
        if (Math.abs(angle) > .5) { // rotate or arc
            double turnRad = Math.toRadians(angle);
            double radius = event.getArcRadius(); // zero for rotate
            dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
            dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));
        } else if (Math.abs(distance) > .01) { // travel
            dx = distance * (float) Math.cos(headingRad);
            dy = distance * (float) Math.sin(headingRad);
        }
        
        heading = normalize(heading + angle); // keep angle between -180 and 180
        x += dx;
        y += dy;		
	}

    /*
     * returns equivalent angle between -180 and +180
     */
    private float normalize(float angle) {
        float a = angle;
        while (a > 180) {
            a -= 360;
        }
        while (a < -180) {
            a += 360;
        }
        return a;
    }
    
    public void setPosition(Point p) {
    	x = p.x;
    	y = p.y;
    }
    
    void setHeading(float heading) {
    	this.heading = heading;
    }
}
