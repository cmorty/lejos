package lejos.robotics.proposal;

import lejos.geom.Point;
import lejos.robotics.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public class WayPoint extends Point {
	protected float heading = 0;
	protected boolean headingRequired;
	protected float maxPositionError = -1;
	protected float maxHeadingError = -1;
	
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
	
	public float getMaxPositionError() {
		return maxPositionError;
	}
	
	public void setMaxPositionError(float distance) {
		maxPositionError = distance;
	}
	
	public float getMaxHeadingError() {
		return maxHeadingError;
	}
	
	public void setMaxHeadingError(float distance) {
		maxHeadingError = distance;
	}
	
	public Pose getPose() {
		return new Pose(x,y,heading);
	}
	
	/**
	 * Check that the given pose satisfies the conditions for this way point
	 * @param p the Pose
	 */
	public boolean checkValidity(Pose p) {
		if (maxPositionError >= 0 && 
		    p.distanceTo(this) > maxPositionError) return false;
		if (headingRequired && maxHeadingError >= 0 && 
			    Math.abs(p.getHeading() - heading) > maxPositionError) return false;
		return true;
	}
}
