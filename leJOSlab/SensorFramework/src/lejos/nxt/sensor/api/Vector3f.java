package lejos.nxt.sensor.api;

/**
 * A 3-element vector that is represented by single-precision floating point 
 * x,y,z coordinates. <p>
 * This class is a placeholder for the javax.vecmath.Vector3f class. 
 * The vecmath package is not included (yet) in Lejos. But Lejos could benefit from it when dealing
 * with data from triaxis sensors.
 *
 */
public class Vector3f {
	public float x=0,y=0,z=0;
	
	public void set(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
}
