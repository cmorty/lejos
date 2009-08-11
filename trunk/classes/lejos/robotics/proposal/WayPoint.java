package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;

public class WayPoint extends Point {
	protected float heading = 0;
	protected boolean headingRequired;
	protected float maxDistanceError = -1;
	
	public WayPoint(float x, float y) {
		super(x,y);
		headingRequired = false; 
	}
	
	public WayPoint(Point p) {
		super((float) p.getX(),(float) p.getY());
		headingRequired = true;
	}
	
	public WayPoint(Pose p) {
		super(p.getX(),p.getY());
		headingRequired = true;
		this.heading = p.getHeading();
	}
	
	public float getHeading() {
		return heading;
	}
	
	public float getMaxDistanceError() {
		return maxDistanceError;
	}
	
	public void setMaxDistanceError(float distance) {
		maxDistanceError = distance;
	}
	
	public Pose getPose() {
		return new Pose(x,y,heading);
	}
}
